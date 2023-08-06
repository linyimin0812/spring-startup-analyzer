package io.github.linyimin0812.profiler.extension.enhance.sample.asyncprofiler;

import io.github.linyimin0812.profiler.common.logger.LogFactory;
import io.github.linyimin0812.profiler.common.settings.ProfilerSettings;
import io.github.linyimin0812.profiler.common.utils.NameUtil;
import io.github.linyimin0812.profiler.common.utils.OSUtil;
import io.github.linyimin0812.profiler.extension.enhance.sample.AsyncProfilerListener;
import io.github.linyimin0812.profiler.extension.enhance.sample.Profiler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.CodeSource;

/**
 * @author yiminlin
 **/
public class AsyncProfiler implements Profiler {

    private final Logger logger = LogFactory.getStartupLogger();

    @Override
    public void start() {
        long interval = Long.parseLong(ProfilerSettings.getProperty(SAMPLE_INTERVAL_MILLIS_CONFIG_ID, "10")) * 1000_000;

        String command;

        String sampleThreadNames = ProfilerSettings.getProperty(SAMPLE_THREAD_NAME_CONFIG_ID);
        if (sampleThreadNames == null || StringUtils.isBlank(sampleThreadNames)) {
            command = String.format("start,event=wall,threads,interval=%s,total", interval);
        } else {
            command = String.format("start,event=wall,threads,interval=%s,total,threadnames=%s", interval, sampleThreadNames);
        }

        try {

            io.github.linyimin0812.profiler.extension.enhance.sample.asyncprofiler.one.AsyncProfiler instance = io.github.linyimin0812.profiler.extension.enhance.sample.asyncprofiler.one.AsyncProfiler.getInstance(getProfilerSoPath());
            if (instance == null) {
                logger.warn("AsyncProfiler instance is null, can't execute start command.");
                return;
            }

            String result = instance.execute(command);
            logger.info("AsyncProfiler execute command: {}, result is {}", command, result);
        } catch (IOException e) {
            logger.error("AsyncProfiler execute command: {} error. error: ", command, e);
        }
    }

    @Override
    public void stop() {

        String command = "stop" + ",file=" + getFile();

        try {
            io.github.linyimin0812.profiler.extension.enhance.sample.asyncprofiler.one.AsyncProfiler instance = io.github.linyimin0812.profiler.extension.enhance.sample.asyncprofiler.one.AsyncProfiler.getInstance();
            if (instance == null) {
                logger.warn("AsyncProfiler instance is null, can't execute stop command.");
                return;
            }

            String result = instance.execute(command);
            logger.info("AsyncProfiler execute stop command: {}, result is {}", command, result);

        } catch (IOException e) {
            logger.error("AsyncProfiler execute stop command error. command: {}, error:", command, e);
        }

    }

    private String getProfilerSoPath() {

        String profilerSoPath = null;

        if (OSUtil.isMac()) {
            profilerSoPath = "async-profiler/libasyncProfiler-mac.so";
        } else if (OSUtil.isLinux()) {
            if (OSUtil.isX86_64() && OSUtil.isMuslLibc()) {
                profilerSoPath = "async-profiler/libasyncProfiler-linux-musl-x64.so";
            } else if(OSUtil.isX86_64()){
                profilerSoPath = "async-profiler/libasyncProfiler-linux-x64.so";
            } else if (OSUtil.isArm64() && OSUtil.isMuslLibc()) {
                profilerSoPath = "async-profiler/libasyncProfiler-linux-musl-arm64.so";
            } else if (OSUtil.isArm64()) {
                profilerSoPath = "async-profiler/libasyncProfiler-linux-arm64.so";
            }

            if (profilerSoPath == null) {
                logger.warn("Current arch do not support AsyncProfiler, Only support X86_64/Arm64/MuslLibc.");
                return null;
            }

            logger.info("getProfilerSoPath: {}", profilerSoPath);

        } else {
            logger.warn("Current OS do not support AsyncProfiler, Only support Linux/Mac.");
            return null;
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
        if (!file.exists() && !file.mkdirs()) {
            logger.warn("create dir: {} failed.", NameUtil.getOutputPath());
        }

        return NameUtil.getOutputPath() + NameUtil.getFlameGraphHtmlName();
    }
}
