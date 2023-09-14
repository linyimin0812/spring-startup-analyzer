package io.github.linyimin0812.profiler.core.monitor.check;

import io.github.linyimin0812.profiler.common.settings.ProfilerSettings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.net.URL;
import java.time.Duration;

/**
 * @author linyimin
 **/
class TimeoutCheckServiceTest {

    private final TimeoutCheckService timeoutCheckService = new TimeoutCheckService();

    @Test
    void init() throws NoSuchFieldException, IllegalAccessException {
        URL configUrl = EndpointCheckServiceTest.class.getClassLoader().getResource("spring-startup-analyzer.properties");
        assert configUrl != null;
        ProfilerSettings.loadProperties(configUrl.getPath());

        timeoutCheckService.init();

        Field durationField = timeoutCheckService.getClass().getDeclaredField("duration");
        durationField.setAccessible(true);

        Duration duration = (Duration) durationField.get(timeoutCheckService);

        Assertions.assertNotNull(duration);
        Assertions.assertEquals(60, duration.getSeconds());
    }

    @Test
    void check() {
        timeoutCheckService.init();
        Assertions.assertEquals(AppStatus.initializing, timeoutCheckService.check());
    }
}