package com.github.linyimin.profiler.common.settings;

import ch.qos.logback.classic.Logger;
import com.github.linyimin.profiler.common.logger.LogFactory;
import com.github.linyimin.profiler.common.utils.OSUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author linyimin
 * @date 2023/04/23 22:00
 **/
public class ProfilerSettings {

    private final static Logger logger = LogFactory.getStartupLogger();
    private final static Properties properties = new Properties();

    static {

        String settingFile = OSUtil.home() + "config" + File.separator + "java-profiler.properties";

        try (FileInputStream fileInputStream = new FileInputStream(settingFile)) {
            properties.load(fileInputStream);
            logger.info("loaded settings from {}", settingFile);
        } catch (IOException e) {
            logger.error("load settings from {} error.", settingFile, e);
        }
    }

    public static String getProperty(String key, String defaultValue) {
        if (System.getProperties().contains(key)) {
            return System.getProperty(key);
        }

        if(properties.containsKey(key)) {
            return properties.getProperty(key);
        }

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

}
