package io.github.linyimin0812.profiler.common.logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author linyimin
 **/
public class Logger {
    private final String LOG_PATH;
    private final PrintWriter LOGGER_WRITER;

    public Logger(LoggerName loggerName, String path) {

        this.LOG_PATH = path + File.separator + loggerName.getValue() + ".log";
        try {
            Path filePath = Paths.get(this.LOG_PATH);
            if (!Files.exists(filePath)) {
                Files.createDirectories(filePath.getParent());
                Files.createFile(filePath);
            }
            FileWriter fileWriter = new FileWriter(this.LOG_PATH, true);
            LOGGER_WRITER = new PrintWriter(fileWriter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void debug(Class<?> clazz, String format, Object... arguments) {
        String baseFormat = basFormat(Level.DEBUG, clazz);
        format = format.replace("{}", "%s");
        String message = String.format(format, arguments);
        write(baseFormat + message);
    }

    public void debug(Class<?> clazz, String message) {
        String baseFormat = basFormat(Level.DEBUG, clazz);
        write(baseFormat + message);
    }

    public void warn(Class<?> clazz, String format, Object... arguments) {
        String baseFormat = basFormat(Level.WARN, clazz);
        format = format.replace("{}", "%s");
        String message = String.format(format, arguments);
        write(baseFormat + message);
    }

    public void warn(Class<?> clazz, String message) {
        String baseFormat = basFormat(Level.WARN, clazz);
        write(baseFormat + message);
    }

    public void info(Class<?> clazz, String format, Object... arguments) {
        String baseFormat = basFormat(Level.INFO, clazz);
        format = format.replace("{}", "%s");
        String message = String.format(format, arguments);
        write(baseFormat + message);
    }

    public void info(Class<?> clazz, String message) {
        String baseFormat = basFormat(Level.INFO, clazz);
        write(baseFormat + message);
    }

    public void error(Class<?> clazz, String format, Object... arguments) {
        String baseFormat = basFormat(Level.ERROR, clazz);
        format = format.replace("{}", "%s");
        String message = String.format(format, arguments);
        write(baseFormat + message);
    }

    public void error(Class<?> clazz, String message) {
        String baseFormat = basFormat(Level.ERROR, clazz);
        write(baseFormat + message);
    }

    public void error(Class<?> clazz, Throwable throwable) {
        String baseFormat = basFormat(Level.ERROR, clazz);
        write(baseFormat + throwable.getMessage());
    }

    private String getCurrentTime() {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        return currentTime.format(formatter);
    }

    private String basFormat(Level level, Class<?> clazz) {
        // yyyy-MM-dd HH:mm:ss.SSS level [thread] class - msg
        String threadName = Thread.currentThread().getName();
        return String.format("%s %s [%s] %s - ", getCurrentTime(), level.name(), threadName, clazz.getName());
    }

    private void write(String message) {
        LOGGER_WRITER.println(message);
        LOGGER_WRITER.flush();
    }

    public void close() {
        if (LOGGER_WRITER == null) {
            return;
        }

        LOGGER_WRITER.close();
    }

    String path() {
        return LOG_PATH;
    }
}
