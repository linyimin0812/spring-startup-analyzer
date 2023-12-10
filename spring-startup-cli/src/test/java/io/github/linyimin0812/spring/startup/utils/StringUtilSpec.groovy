package io.github.linyimin0812.spring.startup.utils

import io.github.linyimin0812.spring.startup.constant.Constants
import spock.lang.Specification

/**
 * @author linyimin
 * */
class StringUtilSpec extends Specification {

    def "test isEmpty"() {
        when:
        def empty = '';
        def content = 'text'

        then:
        StringUtil.isEmpty(empty)
        !StringUtil.isEmpty(content)
    }

    def "test isNotEmpty"() {
        when:
        def empty = '';
        def content = 'text'

        then:
        !StringUtil.isNotEmpty(empty)
        StringUtil.isNotEmpty(content)
    }

    def "test rightPad"() {
        when:
        String content = "test"
        String rightPad = StringUtil.rightPad(content + Constants.SPACE, 10, ".")

        then:
        'test .....' == rightPad
    }
}
