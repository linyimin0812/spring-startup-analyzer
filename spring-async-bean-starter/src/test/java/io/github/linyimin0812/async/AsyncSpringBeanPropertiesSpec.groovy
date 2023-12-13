package io.github.linyimin0812.async

import org.springframework.boot.test.context.runner.ApplicationContextRunner
import spock.lang.Shared
import spock.lang.Specification

/**
 * @author linyimin
 * */
class AsyncSpringBeanPropertiesSpec extends Specification {
    @Shared
    ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(AsyncBeanAutoConfiguration.class)
            .withPropertyValues("spring-startup-analyzer.boost.spring.async.bean-names=testBean");

    def "test propertiesLoading"() {

        given:
        AsyncSpringBeanProperties properties = null

        when:
        this.contextRunner.run(context -> properties = context.getBean(AsyncSpringBeanProperties.class))

        then:
        String.join(",", properties.getBeanNames()) == 'testBean'
    }
}
