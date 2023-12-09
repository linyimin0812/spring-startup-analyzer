package io.github.linyimin0812.profiler.core.monitor.check

import io.github.linyimin0812.profiler.common.settings.ProfilerSettings
import spock.lang.Shared
import spock.lang.Specification

import java.lang.reflect.Field
import java.time.Duration

/**
 * @author linyimin
 * */
class TimeoutCheckServiceSpec extends Specification {

    @Shared
    final TimeoutCheckService timeoutCheckService = new TimeoutCheckService();

    def "test init"() {
        when:
        URL configUrl = TimeoutCheckServiceTest.class.getClassLoader().getResource("spring-startup-analyzer.properties")
        assert configUrl != null
        ProfilerSettings.loadProperties(configUrl.getPath())
        timeoutCheckService.init()

        Field durationField = timeoutCheckService.getClass().getDeclaredField("duration")
        durationField.setAccessible(true)

        Duration duration = (Duration) durationField.get(timeoutCheckService)

        then:
        duration != null
        duration.getSeconds() == 60
    }

    def "test check"() {
        when:
        timeoutCheckService.init()

        then:
        AppStatus.initializing == timeoutCheckService.check()
    }

}
