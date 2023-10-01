package io.github.linyimin0812.spring.startup.utils;

import java.util.Locale;

/**
 * @author linyimin
 **/
public class OSUtil {
    public static boolean isUnix() {
        String os = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);

        return os.startsWith("linux") || os.startsWith("mac") || os.startsWith("darwin");
    }

    public static boolean isWindows() {
        String os = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
        return os.startsWith("windows");
    }
}
