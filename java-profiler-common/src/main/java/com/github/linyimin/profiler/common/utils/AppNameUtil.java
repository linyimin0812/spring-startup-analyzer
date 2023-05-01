package com.github.linyimin.profiler.common.utils;

import java.io.File;

/**
 * @author linyimin
 * @date 2023/04/12 21:13
 **/
public class AppNameUtil {

    private static String appName;

    // 来自sentinel的实现
    private static void resolveAppName() {
        appName = System.getProperty("project.name");
        // use -Dproject.name first
        if (appName != null && !appName.isEmpty()) {
            return;
        }

        appName = System.getProperty("spring.application.name");

        if (appName != null && !appName.isEmpty()) {
            return;
        }

        // parse sun.java.command property
        String command = System.getProperty("sun.java.command");
        if (command == null || command.isEmpty()) {
            return;
        }
        command = command.split("\\s")[0];
        String separator = File.separator;
        if (command.contains(separator)) {
            String[] strs;
            if ("\\".equals(separator)) {
                strs = command.split("\\\\");
            } else {
                strs = command.split(separator);
            }
            command = strs[strs.length - 1];
        }
        if (command.toLowerCase().endsWith(".jar")) {
            command = command.substring(0, command.length() - 4);
        }
        appName = command;
    }

    public static String getAppName() {

        if (appName == null) {
            resolveAppName();
        }

        return appName;
    }
}
