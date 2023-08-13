package io.github.linyimin0812.async.processor;

import io.github.linyimin0812.async.bean.AsyncInitBeanFinder;
import io.github.linyimin0812.async.config.AsyncBeanProperties;
import io.github.linyimin0812.async.config.AsyncConfig;
import io.github.linyimin0812.async.executor.AsyncTaskExecutor;
import io.github.linyimin0812.profiler.common.logger.LogFactory;
import io.github.linyimin0812.profiler.common.logger.Logger;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * @author yiminlin
 **/
public class AsyncProxyBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware, PriorityOrdered {

    private final Logger logger = LogFactory.getStartupLogger();

    private final ThreadLocal<Object> originBeanThreadLocal = new ThreadLocal<>();

    private ConfigurableListableBeanFactory beanFactory;


    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        if (!beanFactory.containsBeanDefinition(beanName)) {
            return bean;
        }

        String methodName = AsyncInitBeanFinder.getAsyncInitMethodName(beanName, beanFactory.getBeanDefinition(beanName));

        if (methodName == null) {
            return bean;
        }

        originBeanThreadLocal.set(bean);

        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTargetClass(bean.getClass());
        proxyFactory.setProxyTargetClass(true);

        AsyncInitializeBeanMethodInvoker invoker = new AsyncInitializeBeanMethodInvoker(bean, beanName, methodName);

        proxyFactory.addAdvice(invoker);

        return proxyFactory.getProxy();

    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        try {
            return originBeanThreadLocal.get() != null ? originBeanThreadLocal.get() : bean;
        } finally {
            originBeanThreadLocal.remove();
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        beanFactory = ((ConfigurableApplicationContext) applicationContext).getBeanFactory();

        AsyncBeanProperties properties = AsyncBeanProperties.parse(applicationContext.getEnvironment());
        AsyncConfig.getInstance().setAsyncBeanProperties(properties);
    }

    class AsyncInitializeBeanMethodInvoker implements MethodInterceptor {

        private final Object targetObject;
        private final String beanName;
        private final String asyncMethodName;


        public AsyncInitializeBeanMethodInvoker(Object targetObject, String beanName, String asyncMethodName) {
            this.targetObject = targetObject;
            this.beanName = beanName;
            this.asyncMethodName = asyncMethodName;
        }

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            if (AsyncTaskExecutor.isFinished()) {
                return invocation.getMethod().invoke(targetObject, invocation.getArguments());
            }

            Method method = invocation.getMethod();
            String methodName = method.getName();

            if (this.asyncMethodName.equals(methodName)) {
                logger.info(AsyncProxyBeanPostProcessor.class, "async-init-bean, beanName: {}, async init method: {}", beanName, asyncMethodName);
                AsyncTaskExecutor.submitTask(() -> {
                    try {
                        long start = System.currentTimeMillis();
                        invocation.getMethod().invoke(targetObject, invocation.getArguments());
                        logger.info(AsyncProxyBeanPostProcessor.class, "async-init-bean, beanName: {}, async init method: {}, cost: {}", beanName, asyncMethodName, System.currentTimeMillis() - start);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                });

                return null;
            }

            return invocation.getMethod().invoke(targetObject, invocation.getArguments());
        }
    }
}
