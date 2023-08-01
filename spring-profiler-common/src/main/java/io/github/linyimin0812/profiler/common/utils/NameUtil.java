package io.github.linyimin0812.profiler.common.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author linyimin
 **/
public class NameUtil {

    private static String appName;
    private static String startupInstanceName;

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

    public static String getStartupInstanceName() {

        if (startupInstanceName == null) {
            String currentTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            startupInstanceName = String.format("%s-%s-%s", NameUtil.getAppName(), currentTime, IpUtil.getIp());
        }

        return startupInstanceName;
    }

    public static String getFlameGraphHtmlName() {
        return getStartupInstanceName() + "-flame-graph.html";
    }

    public static String getAnalysisHtmlName() {
        return getStartupInstanceName() + "-analyzer.html";
    }

    public static String getOutputPath() {
        return AgentHomeUtil.home() + File.separator + "output" + File.separator;
    }

    public static String getTemplatePath() {
        return AgentHomeUtil.home() + File.separator + "template" + File.separator;
    }
}
