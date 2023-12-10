package io.github.linyimin0812.spring.startup.utils

import spock.lang.Specification

/**
 * @author linyimin
 * */
class OSUtilSpec extends Specification {

    def "test isUnix"() {
        when:
        System.setProperty('os.name', 'linux')

        then:
        OSUtil.isUnix()
    }

    def "test isWindows"() {
        when:
        System.setProperty('os.name', 'windows')

        then:
        OSUtil.isWindows()
    }
}
