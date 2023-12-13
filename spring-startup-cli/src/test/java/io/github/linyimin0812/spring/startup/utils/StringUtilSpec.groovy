package io.github.linyimin0812.spring.startup.utils

import io.github.linyimin0812.spring.startup.constant.Constants
import spock.lang.Specification

/**
 * @author linyimin
 * */
class StringUtilSpec extends Specification {

    def "test isEmpty"() {
        when:
        def isEmpty = StringUtil.isEmpty(text)

        then:
        isEmpty == result

        where:
        text || result
        '' || true
        'text' || false

    }

    def "test isNotEmpty"() {
        when:
        def isNotEmpty = StringUtil.isNotEmpty(text)

        then:
        isNotEmpty == reuslt

        where:
        text || reuslt
        '' || false
        'text' || true
    }

    def "test rightPad"() {

        given:
        String content = "test"

        when:
        String rightPad = StringUtil.rightPad(content + Constants.SPACE, 10, ".")

        then:
        'test .....' == rightPad
    }
}
