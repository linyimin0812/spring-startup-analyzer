package io.github.linyimin0812.profiler.common.utils

import spock.lang.Specification

/**
 * @author linyimin
 * */
class IpUtilSpec extends Specification {
    def "test getIp" () {
        when:
        def ip = IpUtil.getIp();

        then:
        ip != null

        String regex = '^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$'

        ip ==~ regex

    }
}
