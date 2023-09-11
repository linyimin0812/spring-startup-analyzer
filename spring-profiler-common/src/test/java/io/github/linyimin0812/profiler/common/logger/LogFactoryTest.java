package io.github.linyimin0812.profiler.common.logger;

import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;


/**
 * @author linyimin
 **/
class LogFactoryTest {

    @Test
    void getStartupLogger() {
        Logger logger = LogFactory.getStartupLogger();
        assertNotNull(logger);
    }

    @Test
    void getTransFormLogger() {
        Logger logger = LogFactory.getTransFormLogger();
        assertNotNull(logger);
    }

    @Test
    void getAsyncInitBeanLogger() {
        Logger logger = LogFactory.getAsyncBeanLogger();
        assertNotNull(logger);
    }

    @Test
    void close() {
        LogFactory.close();
        assertTrue(true);
    }

    @Test
    void createLogger() throws URISyntaxException {
        Logger logger = LogFactory.getStartupLogger();
        URL url = LogFactoryTest.class.getClassLoader().getResource("spring-startup-analyzer.properties");
        assert url != null;

        Path path = Paths.get(url.toURI());
        LogFactory.createLogger(LoggerName.STARTUP, path.getParent().toUri().getPath());

        assertNotEquals(logger, LogFactory.getStartupLogger());

    }
}