package io.github.linyimin0812.profiler.extension.enhance.springbean

import spock.lang.Shared
import spock.lang.Specification

/**
 * @author linyimin
 * */
class BeanCreateListenerSpec extends Specification {

    @Shared
    final BeanCreateListener beanCreateListener = new BeanCreateListener();

    def "test filter with class name"() {
        when:
            def filter = beanCreateListener.filter(className)
        then:
            filter == result
        where:
        className || result
        'org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory' || true
        'AbstractAutowireCapableBeanFactory' || false
    }


    def "test filter with method name and method types"() {
        when:
        def filter = beanCreateListener.filter(methodName, methodTypes)

        then:
        filter == result

        where:
        methodName | methodTypes || result
        'createBean' |  new String[] { "java.lang.String", "org.springframework.beans.factory.support.RootBeanDefinition", "java.lang.Object[]"} || true
        'createBean' | new String[] {} || false
    }
}
