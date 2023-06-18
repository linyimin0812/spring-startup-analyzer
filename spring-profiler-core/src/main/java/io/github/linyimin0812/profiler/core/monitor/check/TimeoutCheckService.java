package io.github.linyimin0812.profiler.core.monitor.check;


import io.github.linyimin0812.profiler.common.logger.LogFactory;
import io.github.linyimin0812.profiler.common.settings.ProfilerSettings;
import org.kohsuke.MetaInfServices;
import org.slf4j.Logger;

import java.time.Duration;
import java.time.Instant;

/**
 * 启动超时检查
 * @author linyimin
 **/
@MetaInfServices
public class TimeoutCheckService implements AppStatusCheckService {

    private final Logger logger = LogFactory.getStartupLogger();
    /**
     * 超时时间: 默认20分钟
     */
    private Duration duration;
    private Instant startInstant;

    @Override
    public void init() {
        long minutes = Long.parseLong(ProfilerSettings.getProperty("spring-startup-analyzer.app.health.check.timeout", "20"));
        duration = Duration.ofMinutes(minutes);
        startInstant = Instant.now();
        logger.info("timeout duration: {} minutes", minutes);
    }

    @Override
    public AppStatus check() {
        Duration runDuration = Duration.between(startInstant, Instant.now());

        // 超时, 默认应用以启动成功
        if (duration.compareTo(runDuration) <= 0) {
            return AppStatus.running;
        }

        return AppStatus.initializing;
    }
}
