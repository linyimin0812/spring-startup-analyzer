package io.github.linyimin0812.profiler.common.logger;

import org.junit.Assert;
import org.junit.Test;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * @author linyimin
 **/
public class LogFactoryTest {

    @Test
    public void getStartupLogger() {
        Logger logger = LogFactory.getStartupLogger();
        Assert.assertNotNull(logger);
    }

    @Test
    public void getTransFormLogger() {
        Logger logger = LogFactory.getTransFormLogger();
        Assert.assertNotNull(logger);
    }

    @Test
    public void close() {
        LogFactory.close();
    }

    @Test
    public void createLogger() throws URISyntaxException {
        Logger logger = LogFactory.getStartupLogger();
        URL url = LogFactoryTest.class.getClassLoader().getResource("spring-startup-analyzer.properties");
        assert url != null;

        Path path = Paths.get(url.toURI());
        LogFactory.createLogger(LoggerName.startup, path.getParent().toUri().getPath());

        Assert.assertNotEquals(logger, LogFactory.getStartupLogger());

    }
}