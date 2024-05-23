package io.github.linyimin0812.profiler.common.logger;

import io.github.linyimin0812.profiler.common.settings.ProfilerSettings;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author linyimin
 **/
public class LogFactory {
    private final static Map<LoggerName, Logger> LOGGER_MAP = new HashMap<>();

    static {
        initialize();
    }

    static void initialize() {
        String defaultLogPath = System.getProperty("user.home") + File.separator + "spring-startup-analyzer" + File.separator + "logs" + File.separator;
        String logPath = ProfilerSettings.getProperty("spring-startup-analyzer.log.path", defaultLogPath);
        if (!logPath.endsWith(File.separator)) {
            logPath = logPath + File.separator;
        }

        LOGGER_MAP.clear();
        createLogger(LoggerName.STARTUP, logPath);
        createLogger(LoggerName.TRANSFORM, logPath);
        createLogger(LoggerName.ASYNC_INIT_BEAN, logPath);
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
}
