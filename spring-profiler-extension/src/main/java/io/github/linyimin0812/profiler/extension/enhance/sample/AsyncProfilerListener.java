package io.github.linyimin0812.profiler.extension.enhance.sample;

import io.github.linyimin0812.profiler.api.EventListener;
import io.github.linyimin0812.profiler.api.event.Event;
import io.github.linyimin0812.profiler.common.logger.LogFactory;
import io.github.linyimin0812.profiler.common.logger.Logger;
import io.github.linyimin0812.profiler.common.settings.ProfilerSettings;
import io.github.linyimin0812.profiler.common.utils.OSUtil;
import io.github.linyimin0812.profiler.extension.enhance.sample.asyncprofiler.AsyncProfiler;
import io.github.linyimin0812.profiler.extension.enhance.sample.jvmprofiler.StacktraceProfiler;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.MetaInfServices;
import java.util.*;

/**
 * @author linyimin
 **/
@MetaInfServices(EventListener.class)
public class AsyncProfilerListener implements EventListener {

    private final Logger logger = LogFactory.getStartupLogger();

    private Profiler profiler;

    @Override
    public boolean filter(String className) {
        //
        return false;
    }

    @Override
    public void onEvent(Event event) {

    }

    @Override
    public List<Event.Type> listen() {
        return Collections.emptyList();
    }

    @Override
    public void start() {
        logger.info(AsyncProfilerListener.class, "==============AsyncProfilerListener start========================");
        logger.info(AsyncProfilerListener.class, "platform:{}, arch: {}", OSUtil.platform(), OSUtil.arch());

        if (OSUtil.isWindows()) {
            profiler = new StacktraceProfiler();
        } else {
            String type = ProfilerSettings.getProperty("spring-startup-analyzer.linux.and.mac.profiler", ProfilerType.ASYNC_PROFILER.name());
            if (StringUtils.endsWithIgnoreCase(type, ProfilerType.ASYNC_PROFILER.name())) {
                profiler = new AsyncProfiler();
            } else {
                profiler = new StacktraceProfiler();
            }
        }

        profiler.start();

    }

    @Override
    public void stop() {
        logger.info(AsyncProfilerListener.class, "==============AsyncProfilerListener stop========================");

        profiler.stop();

    }

    enum ProfilerType {
        JVM_PROFILER,
        ASYNC_PROFILER
    }
}
