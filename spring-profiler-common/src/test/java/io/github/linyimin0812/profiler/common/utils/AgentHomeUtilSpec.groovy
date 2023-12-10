package io.github.linyimin0812.profiler.common.utils

import spock.lang.Specification

/**
 * @author linyimin
 * */
class AgentHomeUtilSpec extends Specification {

    def "test AgentHomeUtil"() {
        when:
        String home = AgentHomeUtil.home();

        then:
        home != null
        home.contains('spring-startup-analyzer')

    }

}
