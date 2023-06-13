package io.github.linyimin0812.profiler.extension.enhance.asyncprofiler;

import io.github.linyimin0812.profiler.api.EventListener;
import io.github.linyimin0812.profiler.api.event.Event;
import io.github.linyimin0812.profiler.common.logger.LogFactory;
import io.github.linyimin0812.profiler.common.settings.ProfilerSettings;
import io.github.linyimin0812.profiler.common.utils.NameUtil;
import io.github.linyimin0812.profiler.common.utils.OSUtil;
import io.github.linyimin0812.profiler.extension.enhance.asyncprofiler.one.profiler.AsyncProfiler;
import org.kohsuke.MetaInfServices;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.util.Collections;
import java.util.List;


/**
 * @author linyimin
 **/
@MetaInfServices(EventListener.class)
public class AsyncProfilerListener implements EventListener {

    private final Logger logger = LogFactory.getStartupLogger();

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
        logger.info("==============AsyncProfilerListener start========================");

        long interval = Long.parseLong(ProfilerSettings.getProperty("spring-startup-analyzer.async.profiler.interval.millis", "10")) * 1000_000;

        String command;

        String sampleThreadNames = ProfilerSettings.getProperty("spring-startup-analyzer.async.profiler.sample.thread.names");
        if (sampleThreadNames == null || sampleThreadNames.length() == 0) {
            command = String.format("start,event=wall,threads,interval=%s,total", interval);
        } else {
            command = String.format("start,event=wall,threads,interval=%s,total,threadnames=%s", interval, sampleThreadNames);
        }

        try {
            String result = AsyncProfiler.getInstance(getProfilerSoPath()).execute(command);
            logger.info("AsyncProfiler execute command: {}, result is {}", command, result);
        } catch (IOException e) {
            logger.error("AsyncProfiler execute command: {} error. error: {}", command, e);
        }
    }

    @Override
    public void stop() {
        logger.info("==============AsyncProfilerListener stop========================");

        long interval = Long.parseLong(ProfilerSettings.getProperty("spring-startup-analyzer.async.profiler.interval.millis", "10")) * 1000_000;

        String command = "stop,interval=" + interval + ",file=" + getFile();

        try {
            String result = AsyncProfiler.getInstance().execute(command);
            logger.info("AsyncProfiler execute stop command: {}, result is {}", command, result);

        } catch (IOException e) {
            logger.info("AsyncProfiler execute stop command error. command: {}, error: {}", command, e);
        }
    }

    private String getProfilerSoPath() {

        String profilerSoPath;

        if (OSUtil.isMac()) {
            profilerSoPath = "async-profiler/libasyncProfiler-mac.so";
        } else if (OSUtil.isLinux()) {
            profilerSoPath = "async-profiler/libasyncProfiler-linux-x64.so";
        } else {
            throw new IllegalStateException("Current OS do not support AsyncProfiler, Only support Linux/Mac.");
        }

        CodeSource source = AsyncProfilerListener.class.getProtectionDomain().getCodeSource();
        if (source == null) {
            throw new IllegalStateException("Can not find libasyncProfiler so, please check the spring-startup-analyzer directory.");
        }

        try {
            File extensionJarPath = new File(source.getLocation().toURI().getSchemeSpecificPart());
            File soFile = new File(extensionJarPath.getParentFile(), profilerSoPath);
            if (soFile.exists()) {
                return soFile.getAbsolutePath();
            } else {
                throw new IllegalStateException("Can not find libasyncProfiler so, please check the spring-startup-analyzer directory.");
            }
        } catch (URISyntaxException e) {
            logger.error("getProfilerSoPath error.", e);
            throw new RuntimeException(e);
        }

    }
    private String getFile() {

        File file = new File(NameUtil.getOutputPath());
        if (!file.exists() && file.mkdirs()) {
            logger.warn("create dir: {} failed.", NameUtil.getOutputPath());
        }

        return NameUtil.getOutputPath() + NameUtil.getFlameGraphHtmlName();
    }
}
