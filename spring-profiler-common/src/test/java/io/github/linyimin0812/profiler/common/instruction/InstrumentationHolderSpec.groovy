package io.github.linyimin0812.profiler.common.instruction

import spock.lang.Specification
import spock.lang.Stepwise

import java.lang.instrument.Instrumentation

/**
 * @author linyimin
 * */
@Stepwise
class InstrumentationHolderSpec extends Specification {

    def "test setInstrumentation"() {
        given:
        Instrumentation instrumentation = Mock()

        when:
        InstrumentationHolder.instrumentation = instrumentation

        then:
        InstrumentationHolder.instrumentation != null
    }

    def "test getInstrumentation"() {
        expect:
        InstrumentationHolder.instrumentation != null
    }
}
