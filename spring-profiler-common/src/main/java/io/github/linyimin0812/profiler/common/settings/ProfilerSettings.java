package io.github.linyimin0812.profiler.common.settings;

import io.github.linyimin0812.profiler.common.utils.AgentHomeUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author linyimin
 **/
public class ProfilerSettings {
    // Due to circular dependency issues with LogFactory, java.util.logging.Logger was used.
    private final static Logger logger = Logger.getLogger(ProfilerSettings.class.getName());

    private final static Properties properties = new Properties();

    static {
        loadProperties(AgentHomeUtil.home() + "config" + File.separator + "spring-startup-analyzer.properties");
    }

    public static String getProperty(String key, String defaultValue) {
        if (isNotBlank(System.getProperty(key))) {
            logger.info("Key: " + key + " from command line, value is " + System.getProperty(key));
            return System.getProperty(key);
        }

        if (properties.containsKey(key)) {
            logger.info("Key: " + key + " from configuration file, value is " + properties.getProperty(key));
            return properties.getProperty(key);
        }

        logger.info("Key: " + key + " not found, use default value: " + defaultValue);

        return defaultValue;
    }

    public static String getProperty(String key) {
        if (isNotBlank(System.getProperty(key))) {
            return System.getProperty(key);
        }
        return properties.getProperty(key);
    }

    public static boolean contains(String key) {
        return properties.containsKey(key);
    }

    public static boolean isNotBlank(String text) {
        return text != null && !text.isEmpty();
    }

    public static void loadProperties(String path) {
        clear();

        try (FileInputStream fileInputStream = new FileInputStream(path)) {
            properties.load(fileInputStream);
            logger.info("loaded settings from " + path);
        } catch (IOException e) {
            logger.severe("load settings from " + path + " error.");
        }
    }

    public static void clear() {
        properties.clear();
    }
}
