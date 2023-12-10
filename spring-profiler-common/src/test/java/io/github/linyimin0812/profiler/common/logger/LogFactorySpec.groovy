package io.github.linyimin0812.profiler.common.logger

import spock.lang.Specification

import java.nio.file.Path
import java.nio.file.Paths


/**
 * @author linyimin
 * */
class LogFactorySpec extends Specification {

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

    def "test getAsyncInitBeanLogger"() {
        when:
        Logger logger = LogFactory.getAsyncBeanLogger()
        then:
        logger != null
    }

    def "test createLogger"() {
        when:
        Logger logger = LogFactory.getStartupLogger();
        URL url = LogFactorySpec.class.getClassLoader().getResource("spring-startup-analyzer.properties")
        Path path = Paths.get(url.toURI());
        LogFactory.createLogger(LoggerName.STARTUP, path.getParent().toUri().getPath())

        then:
        url != null
        logger != LogFactory.getStartupLogger()
    }

}
