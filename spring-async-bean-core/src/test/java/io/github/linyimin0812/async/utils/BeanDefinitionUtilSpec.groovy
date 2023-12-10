package io.github.linyimin0812.async.utils

import io.github.linyimin0812.async.springbeans.SpringFactory
import io.github.linyimin0812.async.springbeans.TestComponentBean
import io.github.linyimin0812.async.springbeans.TestXmlBean
import spock.lang.Specification

/**
 * @author linyimin
 * */
class BeanDefinitionUtilSpec extends Specification {

    def "test isFromConfigurationSource"() {
        when:
        boolean isFrom = BeanDefinitionUtil.isFromConfigurationSource(SpringFactory.getBeanFactory().getBeanDefinition("testComponentBean"))

        then:
        !isFrom
    }

    def "test resolveBeanClassType"() {
        when:
        Class<?> clazz = BeanDefinitionUtil.resolveBeanClassType(SpringFactory.getBeanFactory().getBeanDefinition(beanName));

        then:
        clazz == result

        where:
        beanName || result
        'testXmlBean' || TestXmlBean.class
        'testComponentBean' || TestComponentBean.class
    }
}
