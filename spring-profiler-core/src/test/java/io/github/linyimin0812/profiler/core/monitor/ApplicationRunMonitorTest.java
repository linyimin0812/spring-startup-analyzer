package io.github.linyimin0812.profiler.core.monitor;

import io.github.linyimin0812.profiler.api.event.AtEnterEvent;
import io.github.linyimin0812.profiler.api.event.Event;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author linyimin
 **/
class ApplicationRunMonitorTest {

    private final ApplicationRunMonitor applicationRunMonitor = new ApplicationRunMonitor();

    @Test
    void filter() {
        Assertions.assertTrue(applicationRunMonitor.filter("org.springframework.boot.SpringApplication"));
        Assertions.assertFalse(applicationRunMonitor.filter("org.springframework.boot.ApplicationContext"));
    }

    @Test
    void testFilter() {

        String methodName = "filter";

        String[] methodTypes = null;

        Assertions.assertFalse(applicationRunMonitor.filter(methodName, methodTypes));

        methodTypes = new String[] {"java.lang.Object[]", "java.lang.String[]"};

        Assertions.assertFalse(applicationRunMonitor.filter(methodName, methodTypes));

        methodName = "run";
        Assertions.assertTrue(applicationRunMonitor.filter(methodName, methodTypes));

        methodTypes = new String[] {"java.lang.Class[]", "java.lang.String[]"};

        Assertions.assertTrue(applicationRunMonitor.filter(methodName, methodTypes));

    }

    @Test
    void onEvent() {
        AtEnterEvent event = new AtEnterEvent(0L, 0L, null, null, null, null, null);
        applicationRunMonitor.onEvent(event);
        Assertions.assertTrue(applicationRunMonitor.filter("org.springframework.boot.SpringApplication"));
    }

    @Test
    void listen() {

        List<Event.Type> events = applicationRunMonitor.listen();
        Assertions.assertEquals(1, events.size());
        Assertions.assertEquals(Event.Type.AT_EXIT, events.get(0));
    }

}