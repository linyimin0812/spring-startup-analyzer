package io.github.linyimin0812.profiler.core.monitor;

import io.github.linyimin0812.profiler.api.EventListener;
import io.github.linyimin0812.profiler.api.event.AtExitEvent;
import io.github.linyimin0812.profiler.api.event.Event;
import io.github.linyimin0812.profiler.common.logger.LogFactory;
import io.github.linyimin0812.profiler.common.logger.Logger;
import io.github.linyimin0812.profiler.core.container.IocContainer;
import org.kohsuke.MetaInfServices;

import java.util.Collections;
import java.util.List;

/**
 * @author yiminlin
 **/
@MetaInfServices
public class ApplicationRunMonitor implements EventListener {

    private final Logger logger = LogFactory.getStartupLogger();

    @Override
    public boolean filter(String className) {
        return "org.springframework.boot.SpringApplication".equals(className);
    }

    @Override
    public boolean filter(String methodName, String[] methodTypes) {

        if (!"run".equals(methodName) || methodTypes == null || methodTypes.length != 2) {
            return false;
        }

        return ("java.lang.Class[]".equals(methodTypes[0]) || "java.lang.Object[]".equals(methodTypes[0])) && "java.lang.String[]".equals(methodTypes[1]);
    }

    @Override
    public void onEvent(Event event) {
        if (! (event instanceof AtExitEvent)) {
            return;
        }

        IocContainer.stop();
    }

    @Override
    public List<Event.Type> listen() {
        return Collections.singletonList(Event.Type.AT_EXIT);
    }

    @Override
    public void start() {
        logger.info(ApplicationRunMonitor.class, "=============ApplicationRunMonitor start=============");
    }

    @Override
    public void stop() {
        logger.info(ApplicationRunMonitor.class, "=============ApplicationRunMonitor stop=============");
    }
}
