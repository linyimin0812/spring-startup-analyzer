package io.github.linyimin0812.async.utils;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * @author yiminlin
 * @date 2023/05/14 13:45
 **/
public class BeanDefinitionUtil {

    public static boolean isFromConfigurationSource(BeanDefinition beanDefinition) {
        String beanDefinitionClassName = beanDefinition.getClass().getCanonicalName();

        return beanDefinitionClassName.startsWith("org.springframework.context.annotation.ConfigurationClassBeanDefinitionReader");
    }

    public static Class<?> resolveBeanClassType(BeanDefinition beanDefinition) {

        Class<?> clazz = null;

        if (beanDefinition instanceof AnnotatedBeanDefinition) {
            String className;
            if (isFromConfigurationSource(beanDefinition)) {
                MethodMetadata methodMetadata = ((AnnotatedBeanDefinition) beanDefinition).getFactoryMethodMetadata();
                assert methodMetadata != null;
                className = methodMetadata.getReturnTypeName();
            } else {
                AnnotationMetadata annotationMetadata = ((AnnotatedBeanDefinition) beanDefinition).getMetadata();
                className = annotationMetadata.getClassName();
            }

            try {
                clazz = StringUtils.isEmpty(className) ? null : ClassUtils.forName(className, null);
            } catch (ClassNotFoundException ignore) {
            }
        }

        if (clazz == null) {
            if (beanDefinition instanceof RootBeanDefinition) {
                clazz = ((RootBeanDefinition) beanDefinition).getTargetType();
            }
        }

        if (isCglibProxyClass(clazz)) {
            return clazz.getSuperclass();
        } else {
            return clazz;
        }
    }

    private static boolean isCglibProxyClass(@Nullable Class<?> clazz) {

        if (clazz == null) {
            return false;
        }

        return clazz.getName().contains("$$");
    }
}
