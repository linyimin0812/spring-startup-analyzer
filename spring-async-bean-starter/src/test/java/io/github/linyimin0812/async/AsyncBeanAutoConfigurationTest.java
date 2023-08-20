package io.github.linyimin0812.async;


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
        System.out.println();
        this.contextRunner
                .run(context -> {
                    assertNotNull(context.getBean(AsyncSpringBeanProperties.class));
                });
    }

    @Test
    public void testAsyncTaskExecutionListenerBean() {

    }

    @org.junit.Test
    public void asyncBeanPriorityLoadPostProcessor() {
    }
}