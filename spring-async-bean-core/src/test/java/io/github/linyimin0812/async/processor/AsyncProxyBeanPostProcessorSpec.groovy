package io.github.linyimin0812.async.processor

import io.github.linyimin0812.async.config.AsyncConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.Ordered
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import spock.lang.Specification

/**
 * @author linyimin
 * */
@ContextConfiguration("classpath:bean-context.xml")
@TestPropertySource(locations = "classpath:application.properties")
class AsyncProxyBeanPostProcessorSpec extends Specification {

    @Autowired
    AsyncProxyBeanPostProcessor processor

    def "test getOrder"() {
        expect:
        processor.getOrder() == Ordered.HIGHEST_PRECEDENCE
    }

    def "test setApplicationContext"() {
        expect:
        AsyncConfig.instance.isAsyncBean('testXmlBean') || AsyncConfig.instance.isAsyncBean('testBean')
    }

}
