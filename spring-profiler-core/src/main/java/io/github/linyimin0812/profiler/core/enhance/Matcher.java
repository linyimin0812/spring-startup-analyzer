package io.github.linyimin0812.profiler.core.enhance;

import com.alibaba.deps.org.objectweb.asm.Type;
import com.alibaba.deps.org.objectweb.asm.tree.ClassNode;
import com.alibaba.deps.org.objectweb.asm.tree.MethodNode;
import io.github.linyimin0812.profiler.core.container.IocContainer;
import io.github.linyimin0812.profiler.api.EventListener;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.alibaba.deps.org.objectweb.asm.Opcodes.*;

/**
 * @author linyimin
 **/
public class Matcher {

    private final static Set<String> FILTER_METHODS = new HashSet<>();

    static {
        // 不对类实例初始化方法注入
        FILTER_METHODS.add("<init>");
        // 不对类初始化方法注入
        FILTER_METHODS.add("<clinit>");
        // 不注入类重写的Object父类的方法
        Method[] methods = Object.class.getDeclaredMethods();
        for (Method method : methods) {
            FILTER_METHODS.add(method.getName());
        }
    }

    public static boolean isJavaProfilerFamily(ClassNode classNode) {

        String className = classNode.name;

        if (className.startsWith("com/github/linyimin/profiler")) {
            return true;
        }

        List<String> interfaces = classNode.interfaces;

        return interfaces.stream().anyMatch(name -> name.contains("io/github/linyimin0812/profiler/api"));
    }

    public static boolean isMatchClass(ClassNode classNode) {

        String className = StringUtils.replaceAll(classNode.name, "/", ".");
        return isMatchClass(className);
    }

    public static boolean isMatchClass(String className) {

        for (EventListener listener : IocContainer.getComponents(EventListener.class)) {
            if (listener.filter(className)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isMatchMethod(ClassNode classNode, MethodNode methodNode) {

        // 不对抽象、native等方法注入
        if ((methodNode.access & ACC_ABSTRACT) != 0
                || (methodNode.access & ACC_NATIVE) != 0
                || (methodNode.access & ACC_BRIDGE) != 0
                || (methodNode.access & ACC_SYNTHETIC) != 0
                || (methodNode.access & ACC_VARARGS) != 0) {
            return false;
        }

        if (FILTER_METHODS.contains(methodNode.name)) {
            return false;
        }

        String className = StringUtils.replaceAll(classNode.name, "/", ".");

        Type methodType = Type.getMethodType(methodNode.desc);

        Type[] types = methodType.getArgumentTypes();

        String[] args = new String[types.length];

        for (int i = 0; i < types.length; i++) {
            args[i] = types[i].getClassName();
        }

        for (EventListener listener : IocContainer.getComponents(EventListener.class)) {
            if (listener.filter(className) && listener.filter(methodNode.name, args)) {
                return true;
            }
        }

        return false;
    }
}
