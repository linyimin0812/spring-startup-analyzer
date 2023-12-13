package io.github.linyimin0812.profiler.extension.enhance.invoke

import io.github.linyimin0812.profiler.api.event.Event
import io.github.linyimin0812.profiler.common.settings.ProfilerSettings
import io.github.linyimin0812.profiler.common.ui.MethodInvokeDetail
import spock.lang.Shared
import spock.lang.Specification

import java.lang.reflect.Field

/**
 * @author linyimin
 * */
class InvokeDetailListenerSpec extends Specification {

    @Shared
    final InvokeDetailListener invokeDetailListener = new InvokeDetailListener()

    def setup() {
        URL configurationURL = InvokeDetailListenerSpec.class.getClassLoader().getResource("spring-startup-analyzer.properties")
        assert configurationURL != null
        ProfilerSettings.loadProperties(configurationURL.getPath())

        invokeDetailListener.start()
    }

    def "test filter with class name"() {
        when:
        def filter = invokeDetailListener.filter(className)

        then:
        filter == result

        where:
        className || result
        'java.net.URLClassLoader' || true
        'java.lang.String' || false
    }

    def "test filter with method name and method types"() {
        when:
        def filter = invokeDetailListener.filter(methodName, methodTypes)

        then:
        filter == result

        where:
        methodName | methodTypes || result
        'findResource' | new String[] {'java.lang.String'} || true
        'findResource' | new String[] {} || false
    }

    def "test listen"() {
        when:
        List<Event.Type> list = invokeDetailListener.listen()

        then:
        list.size() == 2
    }

    def "test start"() {
        when:
        Field field = invokeDetailListener.getClass().getDeclaredField("methodQualifiers")
        field.setAccessible(true)
        List<String> methodQualifiers = (List<String>) field.get(invokeDetailListener)

        then:
        methodQualifiers.size() == 1
        methodQualifiers.get(0) == 'java.net.URLClassLoader.findResource(java.lang.String)'
    }

    def "test stop"() {
        when:
        invokeDetailListener.stop();
        Field field = invokeDetailListener.getClass().getDeclaredField("methodQualifiers")
        field.setAccessible(true)
        List<String> methodQualifiers = (List<String>) field.get(invokeDetailListener)

        field = invokeDetailListener.getClass().getDeclaredField("INVOKE_DETAIL_MAP")
        field.setAccessible(true)
        Map<String, MethodInvokeDetail> map = (Map<String, MethodInvokeDetail>) field.get(invokeDetailListener)

        then:
        methodQualifiers.isEmpty()
        map.isEmpty()

    }
}
