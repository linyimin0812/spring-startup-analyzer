package io.github.linyimin0812.profiler.common.logger

import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author linyimin
 * */
class LogFactorySpec extends Specification {

    Path customPathDir = Paths.get(System.getProperty("user.home"), "spring-startup-analyzer", "customlogs", File.separator)

    def setup() {
        System.clearProperty("spring-startup-analyzer.log.path")
        LogFactory.initialize()
    }

    def cleanup() {
        if (Files.exists(customPathDir)) {
            Files.walk(customPathDir)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete)
        }
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
        System.setProperty("spring-startup-analyzer.log.path", customPathDir.toString())
        LogFactory.initialize()

        when:
        Logger logger = LogFactory.getAsyncBeanLogger()
        then:
        logger != null
        logger.path().startsWith(customPathDir.toString())
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

