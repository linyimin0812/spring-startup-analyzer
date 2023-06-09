package io.github.linyimin0812.profiler.extension;

import io.github.linyimin0812.profiler.api.Lifecycle;
import io.github.linyimin0812.profiler.common.logger.LogFactory;
import io.github.linyimin0812.profiler.extension.container.IocContainerHolder;
import org.kohsuke.MetaInfServices;
import org.slf4j.Logger;

/**
 * @author linyimin
 * @date 2023/04/21 17:29
 **/
@MetaInfServices
public class ExtensionLifecycle implements Lifecycle {

    private final Logger logger = LogFactory.getStartupLogger();

    @Override
    public void start() {
        logger.info("============ExtensionLifecycle start=============");
        IocContainerHolder.start();
    }

    @Override
    public void stop() {
        logger.info("============ExtensionLifecycle stop=============");
        IocContainerHolder.stop();
    }
}
