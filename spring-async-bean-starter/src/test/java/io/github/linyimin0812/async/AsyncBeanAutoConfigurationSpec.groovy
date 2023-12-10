package io.github.linyimin0812.async

import io.github.linyimin0812.async.listener.AsyncTaskExecutionListener
import io.github.linyimin0812.async.processor.AsyncBeanPriorityLoadPostProcessor
import io.github.linyimin0812.async.processor.AsyncProxyBeanPostProcessor
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import spock.lang.Shared
import spock.lang.Specification

/**
 * @author linyimin
 * */
class AsyncBeanAutoConfigurationSpec extends Specification {
    @Shared
    ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(AsyncBeanAutoConfiguration.class));

    def "test autoConfigurationProperties"() {
        when:
        AsyncSpringBeanProperties properties = null
        this.contextRunner.run(context -> properties = context.getBean(AsyncSpringBeanProperties.class))

        then:
        properties != null
    }

    def "test asyncTaskExecutionListener"() {
        when:
        AsyncTaskExecutionListener listener = null
        this.contextRunner.run(context -> listener = context.getBean(AsyncTaskExecutionListener.class))

        then:
        listener != null
    }

    def "test asyncBeanPriorityLoadPostProcessor"() {
        when:
        AsyncBeanPriorityLoadPostProcessor processor = null
        this.contextRunner.run(context -> processor = context.getBean(AsyncBeanPriorityLoadPostProcessor.class))

        then:
        processor != null
    }

    def "test asyncProxyBeanPostProcessor"() {
        when:
        AsyncProxyBeanPostProcessor processor = null
        this.contextRunner.run(context -> processor = context.getBean(AsyncProxyBeanPostProcessor.class))

        then:
        processor != null

    }
}
