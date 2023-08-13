package io.github.linyimin0812.profiler.common.logger;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author linyimin
 **/
public class LoggerTest {

    private static Logger logger;

    @BeforeClass
    public static void init() throws URISyntaxException {
        URL url = LogFactoryTest.class.getClassLoader().getResource("spring-startup-analyzer.properties");
        assert url != null;

        String path = Paths.get(url.toURI()).getParent().toUri().getPath();
        logger = new Logger(LoggerName.startup, path);

        Assert.assertTrue(Files.exists(Paths.get(path, LoggerName.startup + ".log")));

    }

    @Test
    public void debug() {
        logger.debug(LoggerTest.class, "debug");
    }

    @Test
    public void testDebug() {
        logger.debug(LoggerTest.class, "debug: {}", "params");
    }

    @Test
    public void warn() {
        logger.warn(LoggerTest.class, "warn");
    }

    @Test
    public void testWarn() {
        logger.warn(LoggerTest.class, "warn: {}", "params");
    }

    @Test
    public void info() {
        logger.info(LoggerTest.class, "info");
    }

    @Test
    public void testInfo() {
        logger.info(LoggerTest.class, "info: {}", "params");
    }

    @Test
    public void error() {
        logger.error(LoggerTest.class, "error");
    }

    @Test
    public void testError() {
        logger.error(LoggerTest.class, "error: {}", "params");
    }

    @Test
    public void testError1() {
        logger.error(LoggerTest.class, "error: {}, {}", "params", new RuntimeException(""));
    }

    @Test
    public void close() {
        logger.close();
    }
}