package io.github.linyimin0812.async.processor

import io.github.linyimin0812.async.config.AsyncBeanProperties
import io.github.linyimin0812.async.config.AsyncConfig
import org.springframework.beans.factory.support.DefaultListableBeanFactory
import spock.lang.Specification

/**
 * @author linyimin
 * */
class AsyncBeanPriorityLoadPostProcessorSpec extends Specification {

    def "test setBeanFactory"() {

        given:
        DefaultListableBeanFactory beanFactory = Mock()
        AsyncBeanProperties properties = new AsyncBeanProperties()
        AsyncConfig.instance.setAsyncBeanProperties(properties)
        AsyncBeanPriorityLoadPostProcessor processor = new AsyncBeanPriorityLoadPostProcessor()

        when: "测试不开启优先加载Bean的情况"
        processor.setBeanFactory(beanFactory)

        then:
        0 * beanFactory.getBean(_)

        when: "测试开启优先加载，但是beanName不存在的情况"

        properties.setBeanPriorityLoadEnable(true)
        beanFactory.containsBeanDefinition('testBean') >> true

        processor.setBeanFactory(beanFactory)

        then:
        0 * beanFactory.getBean(_)

        when: "测试开启优先加载且beanName存在的情况"

        properties.setBeanPriorityLoadEnable(true)
        properties.setBeanNames(['testBean'])

        beanFactory.containsBeanDefinition('testBean') >> true
        processor.setBeanFactory(beanFactory)

        then:
        1 * beanFactory.getBean(_)
    }
}
