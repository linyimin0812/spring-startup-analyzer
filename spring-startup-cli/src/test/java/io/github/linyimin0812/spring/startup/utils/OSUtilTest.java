package io.github.linyimin0812.spring.startup.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author linyimin
 **/
class OSUtilTest {

    @Test
    void isUnix() {
        if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
            assertTrue(OSUtil.isWindows());
        } else {
            assertTrue(OSUtil.isUnix());
        }
    }

    @Test
    void isWindows() {
        if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
            assertTrue(OSUtil.isWindows());
        } else {
            assertTrue(OSUtil.isUnix());
        }
    }
}