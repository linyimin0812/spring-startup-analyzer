package com.github.linyimin.profiler.extension.enhance.asyncprofiler;

import ch.qos.logback.classic.Logger;
import com.github.linyimin.profiler.api.EventListener;
import com.github.linyimin.profiler.api.event.Event;
import com.github.linyimin.profiler.common.logger.LogFactory;
import com.github.linyimin.profiler.common.upload.FileUploader;
import com.github.linyimin.profiler.common.utils.OSUtil;
import com.github.linyimin.profiler.extension.enhance.asyncprofiler.one.profiler.AsyncProfiler;
import com.github.linyimin.profiler.common.jaeger.Jaeger;
import org.kohsuke.MetaInfServices;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.util.*;

/**
 * @author linyimin
 * @date 2023/04/24 10:58
 * @description
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

        String command = "start,event=wall,threads,total,line";

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

        String command = "stop,file=" + getFile();

        try {
            String result = AsyncProfiler.getInstance().execute(command);
            logger.info("AsyncProfiler execute stop command: {}, result is {}", command, result);

            FileUploader.upload(getFile());

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
            if (OSUtil.isArm32()) {
                profilerSoPath = "async-profiler/libasyncProfiler-linux-arm64.so";
            }
        } else {
            throw new IllegalStateException("Current OS do not support AsyncProfiler, Only support Linux/Mac.");
        }

        CodeSource source = AsyncProfilerListener.class.getProtectionDomain().getCodeSource();
        if (source == null) {
            throw new IllegalStateException("Can not find libasyncProfiler so, please check the java-profiler-boost directory.");
        }

        try {
            File extensionJarPath = new File(source.getLocation().toURI().getSchemeSpecificPart());
            File soFile = new File(extensionJarPath.getParentFile(), profilerSoPath);
            if (soFile.exists()) {
                return soFile.getAbsolutePath();
            } else {
                throw new IllegalStateException("Can not find libasyncProfiler so, please check the java-profiler-boost directory.");
            }
        } catch (URISyntaxException e) {
            logger.error("getProfilerSoPath error.", e);
            throw new RuntimeException(e);
        }

    }
    private String getFile() {

        String dir = OSUtil.home() + "output" + File.separator;
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdirs();
        }

        return dir + Jaeger.getServiceName() + ".html";
    }
}
