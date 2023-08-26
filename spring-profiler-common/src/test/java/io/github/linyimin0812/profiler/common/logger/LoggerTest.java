package io.github.linyimin0812.profiler.common.logger;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author linyimin
 **/
class LoggerTest {

    private static Logger logger;

    @BeforeAll
    static void init() throws URISyntaxException {
        URL url = LogFactoryTest.class.getClassLoader().getResource("spring-startup-analyzer.properties");
        assert url != null;

        String path = Paths.get(url.toURI()).getParent().toUri().getPath();
        logger = new Logger(LoggerName.startup, path);

        assertTrue(Files.exists(Paths.get(path, LoggerName.startup + ".log")));

    }

    @Test
    void debug() {
        logger.debug(LoggerTest.class, "debug");
        assertTrue(getLogContent().contains("debug"));

    }

    @Test
    void testDebug() {
        logger.debug(LoggerTest.class, "debug: {}", "params");
        assertTrue(getLogContent().contains("debug: params"));
    }

    @Test
    void warn() {
        logger.warn(LoggerTest.class, "warn");
        assertTrue(getLogContent().contains("warn"));
    }

    @Test
    void testWarn() {
        logger.warn(LoggerTest.class, "warn: {}", "params");
        assertTrue(getLogContent().contains("warn: params"));
    }

    @Test
    void info() {
        logger.info(LoggerTest.class, "info");
        assertTrue(getLogContent().contains("info"));
    }

    @Test
    void testInfo() {
        logger.info(LoggerTest.class, "info: {}", "params");
        assertTrue(getLogContent().contains("info: params"));
    }

    @Test
    void error() {
        logger.error(LoggerTest.class, "error");
        assertTrue(getLogContent().contains("error"));
    }

    @Test
    void testError() {
        logger.error(LoggerTest.class, "error: {}", "params");
        assertTrue(getLogContent().contains("error: params"));
    }

    @Test
    void testError1() {
        logger.error(LoggerTest.class, "error: {}, {}", "params", new RuntimeException(""));
        assertTrue(getLogContent().contains("error: "));
    }

    @Test
    void close() {
        logger.close();
        assertTrue(true);
    }

    private String getLogContent() {

        URL url = LogFactoryTest.class.getClassLoader().getResource("spring-startup-analyzer.properties");
        assert url != null;

        try {
            String path = Paths.get(url.toURI()).getParent().toUri().getPath();

            byte[] bytes = Files.readAllBytes(Paths.get(path, LoggerName.startup + ".log"));

            return new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception ignored) {
            return "";
        }
    }
}