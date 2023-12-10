package io.github.linyimin0812.profiler.agent

import spock.lang.Shared
import spock.lang.Specification

/**
 * @author linyimin
 * */
class ProfilerAgentClassLoaderSpec extends Specification {

    @Shared
    ProfilerAgentClassLoader classLoader

    def setup() {
        URL testJarUrl = ProfilerAgentClassLoaderSpec.class.getClassLoader().getResource("spring-profiler-api.jar")
        classLoader = new ProfilerAgentClassLoader(new URL[] {testJarUrl})
    }

    def "test loadClass"() {

        when:
        Class<?> clazz = classLoader.loadClass(className, true)
        def loaderName = clazz.getClassLoader() == null ? null : clazz.getClassLoader().getClass().getName()

        then:
        loaderName == loader

        where:
        className || loader
        'io.github.linyimin0812.profiler.api.event.AtEnterEvent' || 'io.github.linyimin0812.profiler.agent.ProfilerAgentClassLoader'
        'java.lang.String' || null
    }
}
