package io.github.linyimin0812.profiler.common.utils;

import java.io.File;
import java.util.Locale;

/**
 * @author linyimin
 * @date 2023/04/24 10:40
 * @description retrieve the name of OS
 **/
public class OSUtil {

    private static final String OPERATING_SYSTEM_NAME = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);

    static PlatformEnum platform;

    static {
        if (OPERATING_SYSTEM_NAME.startsWith("linux")) {
            platform = PlatformEnum.LINUX;
        } else if (OPERATING_SYSTEM_NAME.startsWith("mac") || OPERATING_SYSTEM_NAME.startsWith("darwin")) {
            platform = PlatformEnum.MACOS;
        } else if (OPERATING_SYSTEM_NAME.startsWith("windows")) {
            platform = PlatformEnum.WINDOWS;
        } else {
            platform = PlatformEnum.UNKNOWN;
        }

    }

    public static String home() {
        return System.getProperty("user.home") + File.separator + "spring-startup-analyzer" + File.separator;
    }

    private OSUtil() {}

    public static boolean isWindows() {
        return platform == PlatformEnum.WINDOWS;
    }

    public static boolean isLinux() {
        return platform == PlatformEnum.LINUX;
    }

    public static boolean isMac() {
        return platform == PlatformEnum.MACOS;
    }

    private enum PlatformEnum {
        /**
         * Microsoft Windows
         */
        WINDOWS,

        /**
         * A flavor of Linux
         */
        LINUX,
        /**
         * maxOS (OS X)
         */
        MACOS,

        UNKNOWN
    }
}
