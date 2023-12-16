package io.github.linyimin0812.profiler.agent

import spock.lang.Specification

/**
 * @author linyimin
 * */
class ProfilerAgentClassLoaderSpec extends Specification {

    def "test loadClass"() {

        given:
        URL testJarUrl = ProfilerAgentClassLoaderSpec.class.getClassLoader().getResource("spring-profiler-api.jar")
        ProfilerAgentClassLoader classLoader = new ProfilerAgentClassLoader(new URL[] {testJarUrl})

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
