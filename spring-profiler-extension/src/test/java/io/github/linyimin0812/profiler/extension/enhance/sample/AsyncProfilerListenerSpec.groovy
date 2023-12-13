package io.github.linyimin0812.profiler.extension.enhance.sample

import io.github.linyimin0812.profiler.api.event.Event
import spock.lang.Shared
import spock.lang.Specification

/**
 * @author linyimin
 * */
class AsyncProfilerListenerSpec extends Specification {

    @Shared
    AsyncProfilerListener asyncProfilerListener = new AsyncProfilerListener();

    def "test filter with class name"() {
        when:
        def filter = asyncProfilerListener.filter("")

        then:
        !filter
    }

    def "test filter with method name and method types"() {
        when:
        def filter = asyncProfilerListener.filter("", new String[] {})

        then:
        filter
    }

    def "test listen"() {
        when:
        List<Event.Type> list = asyncProfilerListener.listen()

        then:
        list.isEmpty()
    }
}
