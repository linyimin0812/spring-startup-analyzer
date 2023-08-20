package io.github.linyimin0812.async;

import org.junit.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author linyimin
 **/
public class AsyncSpringBeanPropertiesTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(TestConfiguration.class)
            .withPropertyValues("your.async.property=value"); // Customize properties if needed

    @Test
    public void testPropertiesLoading() {
        this.contextRunner.run(context -> {
            AsyncSpringBeanProperties properties = context.getBean(AsyncSpringBeanProperties.class);
        });

    }

    @EnableConfigurationProperties(AsyncSpringBeanProperties.class)
    static class TestConfiguration {
    }

}