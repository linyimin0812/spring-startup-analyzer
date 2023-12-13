package io.github.linyimin0812.async.executor

import spock.lang.Specification


/**
 * @author linyimin
 * */
class NamedThreadFactorySpec extends Specification {

    def "test newThread"() {
        when:
        NamedThreadFactory factory = new NamedThreadFactory("test")
        Thread thread = factory.newThread(() -> {})

        then:
        thread.name =~ 'test-(\\d+)-thread-(\\d+)'
        !thread.isDaemon()
    }
}
