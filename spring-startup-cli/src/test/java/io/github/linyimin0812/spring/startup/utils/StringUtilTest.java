package io.github.linyimin0812.spring.startup.utils;

import io.github.linyimin0812.spring.startup.constant.Constants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author linyimin
 **/
class StringUtilTest {

    @Test
    void isEmpty() {
        Assertions.assertTrue(StringUtil.isEmpty(""));
        Assertions.assertFalse(StringUtil.isEmpty("text"));
    }

    @Test
    void isNotEmpty() {
        Assertions.assertTrue(StringUtil.isNotEmpty("content"));
        Assertions.assertFalse(StringUtil.isNotEmpty(""));
    }

    @Test
    void rightPad() {

        String content = "test";

        String rightPad = StringUtil.rightPad(content + Constants.SPACE, 10, ".");

        Assertions.assertEquals("test .....", rightPad);
    }
}