package io.github.linyimin0812.profiler.common.settings;

import io.github.linyimin0812.profiler.common.logger.LogFactory;
import io.github.linyimin0812.profiler.common.utils.OSUtil;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author linyimin
 **/
public class ProfilerSettings {

    private final static Logger logger = LogFactory.getStartupLogger();
    private final static Properties properties = new Properties();

    static {

        String settingFile = OSUtil.home() + "config" + File.separator + "spring-startup-analyzer.properties";

        try (FileInputStream fileInputStream = new FileInputStream(settingFile)) {
            properties.load(fileInputStream);
            logger.info("loaded settings from {}", settingFile);
        } catch (IOException e) {
            logger.error("load settings from {} error.", settingFile, e);
        }
    }

    public static String getProperty(String key, String defaultValue) {
        if (isNotBlank(System.getProperty(key))) {
            logger.info("Key: {} from command line, value is {}", key, System.getProperty(key));
            return System.getProperty(key);
        }

        if(properties.containsKey(key)) {
            logger.info("Key: {} from configuration file, value is {}", key, properties.getProperty(key));
            return properties.getProperty(key);
        }

        logger.info("Key: {} from default value, value is {}", key, defaultValue);

        return defaultValue;
    }

    public static String getProperty(String key) {
        if (System.getProperties().contains(key)) {
            return System.getProperty(key);
        }
        return properties.getProperty(key);
    }

    public static boolean contains(String key) {
        return properties.containsKey(key);
    }

    public static boolean isNotBlank(String text) {
        return text != null && text.length() > 0;
    }

}
