package io.github.linyimin0812.profiler.api.event

import spock.lang.Specification

/**
 * @author linyimin
 * */
class AtEnterEventSpec extends Specification {

    def "test changeParameter"() {
        given:
        AtEnterEvent enterEvent = new AtEnterEvent(
                1000,
                1001,
                null,
                null,
                null,
                null,
                new Object[] { '1', '2', '3' }
        )

        when:
        enterEvent.changeParameter(index, value)

        then:
        enterEvent.args[index] == value

        where:
        index || value
        0 || 'test1'
        1 || 'test2'
        2 || 'test3'
    }
}
