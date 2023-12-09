package io.github.linyimin0812.profiler.common.settings

import spock.lang.Specification
import spock.lang.Stepwise

/**
 * @author linyimin
 * */
@Stepwise
class ProfilerSettingsSpec extends Specification {

   def "get getProperty"() {
       when:
       URL configurationURL = ProfilerSettingsSpec.class.getClassLoader().getResource("spring-startup-analyzer.properties")
       ProfilerSettings.loadProperties(configurationURL.getPath())
       then:
       ProfilerSettings.getProperty("key") == null
       'default' == ProfilerSettings.getProperty("key", "default")
       'testValue3' == ProfilerSettings.getProperty("testKey3")
       '/xxxxxx/' != ProfilerSettings.getProperty("user.home")
       '/xxxxxx/' != ProfilerSettings.getProperty("user.home", "/xxxxxx/")
    }

    def "test contains"() {
        when:
        URL configurationURL = ProfilerSettingsSpec.class.getClassLoader().getResource("spring-startup-analyzer.properties")
        ProfilerSettings.loadProperties(configurationURL.getPath())

        then:
        ProfilerSettings.contains("testKey2")
        !ProfilerSettings.contains("key")
    }
    def "test isNotBlank"() {
        when:
        URL configurationURL = ProfilerSettingsSpec.class.getClassLoader().getResource("spring-startup-analyzer.properties")
        ProfilerSettings.loadProperties(configurationURL.getPath())

        then:
        ProfilerSettings.isNotBlank("test")
        !ProfilerSettings.isNotBlank("")
    }
}
