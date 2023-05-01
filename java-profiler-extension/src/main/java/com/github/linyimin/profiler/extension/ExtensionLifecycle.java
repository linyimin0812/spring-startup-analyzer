package com.github.linyimin.profiler.extension;

import com.github.linyimin.profiler.common.logger.LogFactory;
import com.github.linyimin.profiler.extension.container.IocContainerHolder;
import com.github.linyimin.profiler.api.Lifecycle;
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
