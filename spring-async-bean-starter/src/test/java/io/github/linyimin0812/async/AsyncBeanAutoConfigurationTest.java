package io.github.linyimin0812.async;


import io.github.linyimin0812.async.listener.AsyncTaskExecutionListener;
import io.github.linyimin0812.async.processor.AsyncBeanPriorityLoadPostProcessor;
import io.github.linyimin0812.async.processor.AsyncProxyBeanPostProcessor;
import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.junit.Assert.*;

/**
 * @author linyimin
 **/
public class AsyncBeanAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(AsyncBeanAutoConfiguration.class));

    @Test
    public void testAutoConfigurationProperties() {
        this.contextRunner.run(context -> assertNotNull(context.getBean(AsyncSpringBeanProperties.class)));
    }

    @Test
    public void testAsyncTaskExecutionListener() {
        this.contextRunner.run(context -> assertNotNull(context.getBean(AsyncTaskExecutionListener.class)));
    }

    @Test
    public void asyncBeanPriorityLoadPostProcessor() {
        this.contextRunner.run(context -> assertNotNull(context.getBean(AsyncBeanPriorityLoadPostProcessor.class)));
    }

    @Test
    public void asyncProxyBeanPostProcessor() {
        this.contextRunner.run(context -> assertNotNull(context.getBean(AsyncProxyBeanPostProcessor.class)));
    }
}