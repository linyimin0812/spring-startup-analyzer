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
import org.springframework.lang.NonNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;


/**
 * @author linyimin
 **/
public class AsyncProxyBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware, PriorityOrdered {

    private final Logger logger = LogFactory.getStartupLogger();

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

        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTargetClass(bean.getClass());
        proxyFactory.setProxyTargetClass(true);

        AsyncInitializeBeanMethodInvoker invoker = new AsyncInitializeBeanMethodInvoker(bean, beanName, methodName);

        proxyFactory.addAdvice(invoker);

        return proxyFactory.getProxy();
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
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
        private final CountDownLatch initCountDownLatch = new CountDownLatch(1);
        /**
         * 标记初始化方法是否在执行中
         */
        private volatile boolean isAsyncCalling = false;
        /**
         * 标记初始化方法是否执行完成
         */
        private volatile boolean isAsyncCalled = false;

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

            if (!isAsyncCalled && this.asyncMethodName.equals(methodName)) {
                isAsyncCalled = true;
                isAsyncCalling = true;
                logger.info(AsyncProxyBeanPostProcessor.class,
                        "async-init-bean, beanName: {}, async init method: {}", beanName, asyncMethodName);
                AsyncTaskExecutor.submitTask(() -> {
                    try {
                        long start = System.currentTimeMillis();
                        invocation.getMethod().invoke(targetObject, invocation.getArguments());
                        logger.info(AsyncProxyBeanPostProcessor.class,
                                "async-init-bean, beanName: {}, async init method: {}, cost: {}",
                                beanName, asyncMethodName, System.currentTimeMillis() - start);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }finally {
                        isAsyncCalling = false;
                        // Wake up the thread waiting for the initialization method to complete
                        initCountDownLatch.countDown();
                    }
                });
                return null;
            }
            // 启动过程中，如果异步方法还在执行中（比如：提交到了线程池，但可能还未被执行），但此时却调用了该对象的其他方法，则等待异步方法执行完成
            if (isAsyncCalling) {
                long startTime = System.currentTimeMillis();
                initCountDownLatch.await();
                logger.warn(AsyncProxyBeanPostProcessor.class,
                        "{}({})-{} method blocked {} ms waiting for {} method initialization to complete.",
                        targetObject.getClass().getName(), beanName, methodName,
                        (System.currentTimeMillis() - startTime), asyncMethodName);
            }

            return invocation.getMethod().invoke(targetObject, invocation.getArguments());
        }
    }
}
