package io.github.linyimin0812.profiler.agent

import spock.lang.Specification

import java.lang.instrument.Instrumentation

/**
 * @author linyimin
 * */
class ProfilerAgentBoostrapSpec extends Specification {

    def "test premain"() {
        given:

        Instrumentation instrumentation = Mock()

        when:
        ProfilerAgentBoostrap.premain(null, instrumentation)

        then:
        thrown(NoClassDefFoundError)
    }
}
