package io.github.linyimin0812.async.bean;

import io.github.linyimin0812.async.config.AsyncConfig;
import io.github.linyimin0812.async.utils.BeanDefinitionUtil;
import io.github.linyimin0812.profiler.common.logger.LogFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.StandardMethodMetadata;
import org.springframework.util.ClassUtils;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author yiminlin
 * @date 2023/05/14 01:37
 **/
public class AsyncInitBeanFinder {

    private static final Logger logger = LogFactory.getStartupLogger();

    public static String getAsyncInitMethodName(String beanName, BeanDefinition beanDefinition) {

        if (beanDefinition == null) {
            logger.warn("The beanName: {} of BeanDefinition is not exist!!!", beanName);
            return null;
        }

        if (!AsyncConfig.getInstance().isAsyncBean(beanName)) {
            return null;
        }

        if (BeanDefinitionUtil.isFromConfigurationSource(beanDefinition)) {
            return scanAsyncInitMethodOnMethod(beanName, (AnnotatedBeanDefinition) beanDefinition);
        } else {

            Class<?> beanClassType = BeanDefinitionUtil.resolveBeanClassType(beanDefinition);

            if (beanClassType == null) {
                return null;
            }

            return scanAsyncInitMethodOnClass(beanName, beanClassType);
        }
    }

    private static String scanAsyncInitMethodOnClass(String beanName, Class<?> beanClassType) {
        List<Method> candidateMethods = new ArrayList<>();

        for (Method method : beanClassType.getDeclaredMethods()) {
            if (AnnotatedElementUtils.hasAnnotation(method, PostConstruct.class)) {
                candidateMethods.add(method);
            }
        }

        if (candidateMethods.isEmpty()) {
            return null;
        }

        if (candidateMethods.size() == 1) {
            return candidateMethods.get(0).getName();
        }

        logger.warn("There are {} @PostConstruct methods, async execution is not performed!!!", candidateMethods.size());

        return null;
    }

    private static String scanAsyncInitMethodOnMethod(String beanName, AnnotatedBeanDefinition beanDefinition) {

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
            return null;
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

        if (candidateMethods.isEmpty()) {
            logger.warn("The beanName: {} of bean is not exist!!!", beanName);

            return null;
        }

        if (candidateMethods.size() == 1) {

            Bean bean = candidateMethods.get(0).getAnnotation(Bean.class);
            return bean == null ? null : bean.initMethod();

        }

        logger.error("Multi @Bean-method with same name try to publish in {}, async execution is not performed!!!", returnType.getCanonicalName());
        return null;
    }

}
