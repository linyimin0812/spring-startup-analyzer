package io.github.linyimin0812.profiler.core.monitor

import io.github.linyimin0812.profiler.api.event.AtEnterEvent
import io.github.linyimin0812.profiler.api.event.Event
import spock.lang.Shared
import spock.lang.Specification

/**
 * @author linyimin
 * */
class ApplicationRunMonitorSpec extends Specification {

    @Shared
    ApplicationRunMonitor applicationRunMonitor = new ApplicationRunMonitor();

    def "test filter with class name"() {
        when:
        def filter = applicationRunMonitor.filter(className)

        then:
        filter == result

        where:
        className || result
        'org.springframework.boot.SpringApplication' || true
        'org.springframework.boot.ApplicationContext' || false
    }

    def "test filter both with class name and method name"() {

        when:
        def filterResult = applicationRunMonitor.filter(methodName, methodTypes as String[])

        then:
        filterResult == result

        where:
        methodName | methodTypes || result
        'filter' | null || false
        'filter' | ['java.lang.Object[]', 'java.lang.String[]'] || false
        'run' | ['java.lang.Object[]', 'java.lang.String[]'] || true
        'run' | ['java.lang.Class[]', 'java.lang.String[]'] || true

    }


    def "test onEvent"() {

        given:
        AtEnterEvent event = new AtEnterEvent(0L, 0L, null, null, null, null, null)

        when:
        applicationRunMonitor.onEvent(event)

        then:
        applicationRunMonitor.filter("org.springframework.boot.SpringApplication")

    }

    def "test listen"() {
        when:
        List<Event.Type> events = applicationRunMonitor.listen()

        then:
        events.size() == 1
        events.get(0) == Event.Type.AT_EXIT
    }
}
