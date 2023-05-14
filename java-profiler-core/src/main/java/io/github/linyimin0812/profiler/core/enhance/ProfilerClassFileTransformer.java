package io.github.linyimin0812.profiler.core.enhance;

import com.alibaba.bytekit.asm.MethodProcessor;
import com.alibaba.bytekit.asm.interceptor.InterceptorProcessor;
import com.alibaba.bytekit.asm.interceptor.parser.DefaultInterceptorClassParser;
import com.alibaba.bytekit.utils.AsmUtils;
import com.alibaba.deps.org.objectweb.asm.ClassReader;
import com.alibaba.deps.org.objectweb.asm.Opcodes;
import com.alibaba.deps.org.objectweb.asm.Type;
import com.alibaba.deps.org.objectweb.asm.tree.ClassNode;
import com.alibaba.deps.org.objectweb.asm.tree.MethodNode;
import io.github.linyimin0812.Bridge;
import io.github.linyimin0812.profiler.common.instruction.InstrumentationHolder;
import io.github.linyimin0812.profiler.common.logger.LogFactory;
import io.github.linyimin0812.profiler.common.utils.MainClassUtil;
import io.github.linyimin0812.profiler.core.container.IocContainer;
import org.slf4j.Logger;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 此类由agent加载，入口为构造函数
 * @author linyimin
 * @date 2023/04/17 17:43
 **/
public class ProfilerClassFileTransformer implements ClassFileTransformer {

    private final Logger logger = LogFactory.getTransFormLogger();

    private final Object DUMMY = new Object();
    private final Map<String, Object> enhancedObject = new ConcurrentHashMap<>();


    public ProfilerClassFileTransformer(Instrumentation instrumentation, String args, List<URL> manifestPackages) {

        Bridge.setBridge(new EventDispatcher());
        InstrumentationHolder.setInstrumentation(instrumentation);

        MainClassUtil.resolveMainClassPackage(manifestPackages);

        IocContainer.start();

    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        if (className == null) {
            return null;
        }

        // 排除系统类及spring中的动态代理类
        if (classBeingRedefined == null && (className.contains("sun/") || className.contains("java/") || className.contains("javax/") || className.contains("CGLIB"))) {
            return null;
        }

        ClassNode classNode = new ClassNode(Opcodes.ASM9);
        ClassReader classReader = AsmUtils.toClassNode(classfileBuffer, classNode);
        classNode = AsmUtils.removeJSRInstructions(classNode);

        // 把自己排除掉
        if (Matcher.isJavaProfilerFamily(classNode)) {
            return null;
        }

        if (!Matcher.isMatchClass(classNode)) {
            return null;
        }

        List<MethodNode> methodNodes = classNode.methods;

        // 生成增强字节码
        DefaultInterceptorClassParser defaultInterceptorClassParser = new DefaultInterceptorClassParser();

        List<InterceptorProcessor> interceptorProcessors = new ArrayList<>(defaultInterceptorClassParser.parse(Interceptor.class));

        for (MethodNode methodNode : methodNodes) {

            if (isEnhanceBefore(loader, classNode, methodNode)) {
                continue;
            }

            if (!Matcher.isMatchMethod(classNode, methodNode)) {
                continue;
            }

            MethodProcessor methodProcessor = new MethodProcessor(classNode, methodNode);
            for (InterceptorProcessor interceptor : interceptorProcessors) {
                try {
                    interceptor.process(methodProcessor);
                    logger.info("transform: {}", getCacheKey(loader, classNode, methodNode));
                } catch (Throwable e) {
                    logger.error("enhancer error, class: {}, method: {}, interceptor: {}, error: {}",
                            classNode.name, methodNode.name, interceptor.getClass().getName(), e);
                }
            }

            cacheEnhanceObject(loader, classNode, methodNode);

        }

        return AsmUtils.toBytes(classNode, loader, classReader);

    }

    public void retransformLoadedClass() {
        Instrumentation instrumentation = InstrumentationHolder.getInstrumentation();

        for (Class<?> loadedClass : instrumentation.getAllLoadedClasses()) {
            if (Matcher.isMatchClass(loadedClass.getName())) {
                try {
                    instrumentation.retransformClasses(loadedClass);
                    logger.info("re-transform class: {}", loadedClass.getName());
                } catch (UnmodifiableClassException e) {
                    logger.error("re-transform class error.", e);
                }
            }
        }
    }

    private void cacheEnhanceObject(ClassLoader loader, ClassNode classNode, MethodNode methodNode) {
        enhancedObject.put(getCacheKey(loader, classNode, methodNode), DUMMY);
    }

    private boolean isEnhanceBefore(ClassLoader loader, ClassNode classNode, MethodNode methodNode) {
        return enhancedObject.containsKey(getCacheKey(loader, classNode, methodNode));
    }

    private String getCacheKey(ClassLoader loader, ClassNode classNode, MethodNode methodNode) {

        String loaderName = loader == null ? "Bootstrap" : loader.getClass().getName();
        String className = classNode.name;
        String methodName = methodNode.name;

        Type methodType = Type.getMethodType(methodNode.desc);
        StringBuilder argTypes = new StringBuilder();

        for (Type type : methodType.getArgumentTypes()) {
            argTypes.append(type.getClassName());
        }

        return loaderName + "#" + className + "#" + methodName + "#" + argTypes;
    }

}
