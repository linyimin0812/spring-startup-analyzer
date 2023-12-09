package io.github.linyimin0812.profiler.common.logger

import spock.lang.Shared
import spock.lang.Specification

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths

/**
 * @author linyimin
 * */
class LoggerSpec extends Specification {

    @Shared
    static Logger logger;

    def setupSpec() {

        URL url = LoggerSpec.class.getClassLoader().getResource("spring-startup-analyzer.properties");
        assert url != null;

        String path = Paths.get(url.toURI()).getParent().toUri().getPath();
        logger = new Logger(LoggerName.STARTUP, path);

        assertTrue(Files.exists(Paths.get(path, LoggerName.STARTUP.getValue() + ".log")));
    }

    def "test debug"() {
        when:
        logger.debug(LoggerSpec.class, "debug")
        then:
        getLogContent().contains("debug")
    }


    def "test debug format"() {
        when:
        logger.debug(LoggerSpec.class, "debug: {}", "params")

        then:
        getLogContent().contains("debug: params")
    }


    def "test warn"() {
        when:
        logger.debug(LoggerSpec.class, "warn")
        then:
        getLogContent().contains("warn")
    }


    def "test warn format"() {
        when:
        logger.debug(LoggerSpec.class, "warn: {}", "params")

        then:
        getLogContent().contains("warn: params")
    }

    def "test info"() {
        when:
        logger.debug(LoggerSpec.class, "info")
        then:
        getLogContent().contains("info")
    }


    def "test info format"() {
        when:
        logger.debug(LoggerSpec.class, "info: {}", "params")

        then:
        getLogContent().contains("info: params")
    }

    def "test error"() {
        when:
        logger.debug(LoggerSpec.class, "error")
        then:
        getLogContent().contains("error")
    }


    def "test error format"() {
        when:
        logger.debug(LoggerSpec.class, "error: {}", "params")

        then:
        getLogContent().contains("error: params")
    }

    def "test error with error"() {
        when:
        logger.error(LoggerSpec.class, "error: {}, {}", "params", new RuntimeException(""))
        then:
        getLogContent().contains("error: ")
    }

    def "test close"() {
        when:
        logger.close()
        then:
        true
    }

    String getLogContent() {

        URL url = LoggerSpec.class.getClassLoader().getResource("spring-startup-analyzer.properties");
        assert url != null;

        try {
            String path = Paths.get(url.toURI()).getParent().toUri().getPath();

            byte[] bytes = Files.readAllBytes(Paths.get(path, LoggerName.STARTUP.getValue() + ".log"));

            return new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception ignored) {
            return "";
        }
    }

}
