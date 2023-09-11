package io.github.linyimin0812.profiler.common.logger;

import io.github.linyimin0812.profiler.common.utils.AgentHomeUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author linyimin
 **/
public class LogFactory {
    private final static Map<LoggerName, Logger> LOGGER_MAP = new HashMap<>();

    static {
        createLogger(LoggerName.STARTUP);
        createLogger(LoggerName.TRANSFORM);

        String asyncInitBeanLogPath = System.getProperty("user.home") + File.separator + "spring-startup-analyzer" + File.separator + "logs" + File.separator;
        createLogger(LoggerName.ASYNC_INIT_BEAN, asyncInitBeanLogPath);
    }

    public static Logger getStartupLogger() {
        return LOGGER_MAP.get(LoggerName.STARTUP);
    }

    public static Logger getTransFormLogger() {
        return LOGGER_MAP.get(LoggerName.TRANSFORM);
    }

    public static Logger getAsyncBeanLogger() {
        return LOGGER_MAP.get(LoggerName.ASYNC_INIT_BEAN);
    }

    public static void close() {
        for (Logger logger : LOGGER_MAP.values()) {
            logger.close();
        }
    }

    public static void createLogger(LoggerName loggerName, String path) {
        Logger logger = new Logger(loggerName, path);
        LOGGER_MAP.put(loggerName, logger);
    }

    private static void createLogger(LoggerName loggerName) {
        createLogger(loggerName, AgentHomeUtil.home() + "logs");
    }

}
