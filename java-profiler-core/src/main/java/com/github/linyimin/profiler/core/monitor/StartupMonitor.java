package com.github.linyimin.profiler.core.monitor;

import ch.qos.logback.classic.Logger;
import com.github.linyimin.profiler.common.logger.LogFactory;
import com.github.linyimin.profiler.core.container.IocContainer;
import com.github.linyimin.profiler.core.monitor.check.AppStatus;
import com.github.linyimin.profiler.core.monitor.check.AppStatusCheckService;
import com.github.linyimin.profiler.api.Lifecycle;
import org.kohsuke.MetaInfServices;

/**
 * @author linyimin
 * @date 2023/04/20 17:47
 * @description 应用启动检测
 **/
@MetaInfServices
public class StartupMonitor implements Lifecycle {

    private final Logger logger = LogFactory.getStartupLogger();

    private void checkStatus() {

        int count = 0;

        while (true) {
            boolean isRunning = IocContainer.getComponents(AppStatusCheckService.class).stream().anyMatch(service -> service.check() == AppStatus.running);
            if (isRunning) {
                break;
            }

            if (count++ % 10 == 0) {
                logger.info("app initializing {} s", count);
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
                break;
            }
        }

        // 应用启动结束后容器终止
        IocContainer.stop();
    }

    @Override
    public void start() {
        logger.info("==========StartupMonitor start========");
        Thread startupMonitorThread = new Thread(this::checkStatus);
        startupMonitorThread.setName("StartupMonitor-Thread");
        startupMonitorThread.start();
    }

    @Override
    public void stop() {
        // 应用启动结束后容器终止
        logger.info("==========StartupMonitor stop========");
    }
}
