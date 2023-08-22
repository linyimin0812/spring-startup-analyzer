package io.github.linyimin0812.async;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;


/**
 * @author linyimin
 **/
public class AsyncSpringBeanPropertiesTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(AsyncBeanAutoConfiguration.class)
            .withPropertyValues("spring-startup-analyzer.boost.spring.async.bean-names=testBean");

    @Test
    public void testPropertiesLoading() {
        this.contextRunner.run(context -> {
            AsyncSpringBeanProperties properties = context.getBean(AsyncSpringBeanProperties.class);
            Assert.assertEquals("testBean", String.join(",", properties.getBeanNames()));
        });

    }
}