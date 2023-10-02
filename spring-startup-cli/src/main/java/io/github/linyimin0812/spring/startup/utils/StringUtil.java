package io.github.linyimin0812.spring.startup.utils;

import io.github.linyimin0812.spring.startup.constant.Constants;

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

    public static String rightPad(final String str, final int size, String padStr) {
        if (str == null) {
            return null;
        }

        padStr = (padStr == null || padStr.isEmpty()) ? Constants.SPACE : padStr;

        final int padLen = padStr.length();
        final int strLen = str.length();
        final int pads = size - strLen;
        if (pads <= 0) {
            return str;
        }

        if (pads == padLen) {
            return str.concat(padStr);
        } else if (pads < padLen) {
            return str.concat(padStr.substring(0, pads));
        } else {
            final char[] padding = new char[pads];
            final char[] padChars = padStr.toCharArray();
            for (int i = 0; i < pads; i++) {
                padding[i] = padChars[i % padLen];
            }
            return str.concat(new String(padding));
        }
    }

}
