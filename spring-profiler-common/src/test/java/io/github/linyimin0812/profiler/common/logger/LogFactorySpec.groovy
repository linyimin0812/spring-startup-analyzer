package io.github.linyimin0812.profiler.common.logger

import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author linyimin
 * */
class LogFactorySpec extends Specification {

    Path tempDir

    def setup() {
        System.clearProperty("spring-startup-analyzer.log.path")
        tempDir = Files.createTempDirectory("test-logs")
        LogFactory.initialize()
    }

    def cleanup() {
        Files.walk(tempDir)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete)
    }

    def "test getStartupLogger"() {
        when:
        Logger logger = LogFactory.getStartupLogger()
        then:
        logger != null
    }

    def "test getTransFormLogger"() {
        when:
        Logger logger = LogFactory.getTransFormLogger()
        then:
        logger != null
    }

    def "test getAsyncInitBeanLogger - default path"() {
        given:
        String defaultLogPath = System.getProperty("user.home") + File.separator + "spring-startup-analyzer" + File.separator + "logs" + File.separator

        when:
        Logger logger = LogFactory.getAsyncBeanLogger()
        then:
        logger != null
        logger.path().startsWith(defaultLogPath)
    }

    def "test getAsyncInitBeanLogger - custom path"() {
        given:
        String customLogPath = tempDir.toString() + File.separator
        System.setProperty("spring-startup-analyzer.log.path", customLogPath)
        LogFactory.initialize()

        when:
        Logger logger = LogFactory.getAsyncBeanLogger()
        then:
        logger != null
        logger.path().startsWith(customLogPath)
    }

    def "test createLogger"() {
        given:
        Logger originalLogger = LogFactory.getStartupLogger()

        when:
        URL url = LogFactorySpec.class.getClassLoader().getResource("spring-startup-analyzer.properties")
        Path path = Paths.get(url.toURI())
        LogFactory.createLogger(LoggerName.STARTUP, path.getParent().toUri().getPath())
        Logger newLogger = LogFactory.getStartupLogger()

        then:
        url != null
        originalLogger != newLogger
    }

}

