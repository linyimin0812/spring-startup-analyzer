package io.github.linyimin0812.async.executor

import spock.lang.Specification

import java.util.regex.Matcher
import java.util.regex.Pattern


/**
 * @author linyimin
 * */
class NamedThreadFactorySpec extends Specification {

    def "test newThread"() {
        when:
        NamedThreadFactory factory = new NamedThreadFactory("test")
        Thread thread = factory.newThread(() -> {})

        Pattern pattern = Pattern.compile("test-(\\d+)-thread-(\\d+)")
        Matcher matcher = pattern.matcher(thread.getName())

        then:
        matcher.matches()
        !thread.isDaemon()
    }
}
