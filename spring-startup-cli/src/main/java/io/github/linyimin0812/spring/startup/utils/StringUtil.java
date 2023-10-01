package io.github.linyimin0812.spring.startup.utils;

/**
 * @author linyimin
 **/
public class StringUtil {
    public static boolean isEmpty(String text) {
        return text == null || text.isEmpty();
    }

    public static boolean isNotEmpty(String text) {
        return text != null && !text.isEmpty();
    }
}
