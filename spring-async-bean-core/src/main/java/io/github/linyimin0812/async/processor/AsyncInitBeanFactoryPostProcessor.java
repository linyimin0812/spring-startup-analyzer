package io.github.linyimin0812.async.processor;

import io.github.linyimin0812.async.annotation.AsyncInit;
import io.github.linyimin0812.async.bean.AsyncInitBeanHolder;
import io.github.linyimin0812.async.config.AsyncBeanProperties;
import io.github.linyimin0812.async.config.AsyncConfig;
import io.github.linyimin0812.async.utils.BeanDefinitionUtil;
import io.github.linyimin0812.profiler.common.logger.LogFactory;
import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.StandardMethodMetadata;
import org.springframework.util.ClassUtils;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author yiminlin
 * @date 2023/05/14 01:40
 **/
public class AsyncInitBeanFactoryPostProcessor implements BeanFactoryPostProcessor, ApplicationContextAware {

    private final Logger logger = LogFactory.getStartupLogger();

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

        Map<String, BeanDefinition> beanDefinitionMap = Arrays.stream(beanFactory.getBeanDefinitionNames())
                .collect(Collectors.toMap(Function.identity(), beanFactory::getBeanDefinition));

        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            scanAsyncInitBeanDefinition(entry.getKey(), entry.getValue(), beanFactory);
        }
    }

    private void scanAsyncInitBeanDefinition(String beanName, BeanDefinition beanDefinition, ConfigurableListableBeanFactory beanFactory) {
        if (BeanDefinitionUtil.isFromConfigurationSource(beanDefinition)) {
            scanAsyncInitMethodOnMethod(beanName, (AnnotatedBeanDefinition) beanDefinition);
        } else {
            Class<?> beanClassType = BeanDefinitionUtil.resolveBeanClassType(beanDefinition);

            if (beanClassType == null) {
                return;
            }

            scanAsyncInitMethodOnClass(beanName, beanClassType, beanDefinition, beanFactory);
        }
    }

    private void scanAsyncInitMethodOnClass(String beanName, Class<?> beanClassType, BeanDefinition beanDefinition, ConfigurableListableBeanFactory beanFactory) {

        AsyncInit asyncInitAnnotation = AnnotationUtils.findAnnotation(beanClassType, AsyncInit.class);

        if (asyncInitAnnotation != null || AsyncConfig.getInstance().isAsyncBean(beanName)) {
            scanAsyncPostConstructMethodOnClass(beanName, beanClassType);
            return;
        }

        scanAsyncPostConstructMethodOnMethod(beanName, beanClassType);
    }

    private void scanAsyncPostConstructMethodOnMethod(String beanName, Class<?> beanClassType) {
        for (Method method : beanClassType.getDeclaredMethods()) {
            if (AnnotatedElementUtils.hasAnnotation(method, AsyncInit.class) && AnnotatedElementUtils.hasAnnotation(method, PostConstruct.class)) {
                AsyncInitBeanHolder.registerAsyncInitBean(beanName, method.getName());
            }
        }
    }

    private void scanAsyncPostConstructMethodOnClass(String beanName, Class<?> beanClassType) {
        for (Method method : beanClassType.getDeclaredMethods()) {
            if (!AnnotatedElementUtils.hasAnnotation(method, PostConstruct.class)) {
                continue;
            }

            AsyncInitBeanHolder.registerAsyncInitBean(beanName, method.getName());
        }
    }

    private void scanAsyncInitMethodOnMethod(String beanName, AnnotatedBeanDefinition beanDefinition) {

        Class<?> returnType;
        Class<?> declaringClass;
        List<Method> candidateMethods = new ArrayList<>();

        MethodMetadata methodMetadata = beanDefinition.getFactoryMethodMetadata();

        try {
            assert methodMetadata != null;
            returnType = ClassUtils.forName(methodMetadata.getReturnTypeName(), null);
            declaringClass = ClassUtils.forName(methodMetadata.getDeclaringClassName(), null);
        } catch (ClassNotFoundException e) {
            logger.error("get returnType and declaringClass error. bean: {}", beanName, e);
            return;
        }

        if (methodMetadata instanceof StandardMethodMetadata) {
            candidateMethods.add(((StandardMethodMetadata) methodMetadata).getIntrospectedMethod());
        } else {
            for (Method method : declaringClass.getDeclaredMethods()) {
                if (!method.getName().equals(methodMetadata.getMethodName()) || !method.getReturnType().getTypeName().equals(methodMetadata.getReturnTypeName())) {
                    continue;
                }
                if (!AnnotatedElementUtils.hasAnnotation(method, Bean.class)) {
                    continue;
                }

                Bean bean = method.getAnnotation(Bean.class);
                Set<String> beanNames = new HashSet<>();
                beanNames.add(method.getName());
                if (bean != null) {
                    beanNames.addAll(Arrays.asList(bean.name()));
                    beanNames.addAll(Arrays.asList(bean.value()));
                }

                if (!beanNames.contains(beanName)) {
                    continue;
                }

                candidateMethods.add(method);
            }
        }

        if (candidateMethods.size() == 1) {
            AsyncInit asyncInitAnnotation = candidateMethods.get(0).getAnnotation(AsyncInit.class);
            if (asyncInitAnnotation == null) {
                asyncInitAnnotation = returnType.getAnnotation(AsyncInit.class);
            }

            if (asyncInitAnnotation == null && !AsyncConfig.getInstance().isAsyncBean(beanName)) {
                return;
            }

            AsyncInitBeanHolder.registerAsyncInitBean(beanName, beanDefinition.getInitMethodName());
        } else if (candidateMethods.size() > 1) {
            // TODO: 支持多个?
            throw new RuntimeException("Multi @Bean-method with same name try to publish in " + returnType.getCanonicalName());
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        AsyncBeanProperties properties = AsyncBeanProperties.parse(applicationContext.getEnvironment());
        AsyncConfig.getInstance().setAsyncBeanProperties(properties);
    }
}
