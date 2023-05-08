package com.github.linyimin.profiler.common.utils;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author linyimin
 * @date 2023/04/24 16:53
 **/

public class OSUtilTest {

    @Test
    public void testIsWindows() {
        if (System.getProperty("os.name").toLowerCase().startsWith("win")) {
            assertTrue(OSUtil.isWindows());
        } else {
            assertFalse(OSUtil.isWindows());
        }
    }

    @Test
    public void testIsLinux() {
        if (System.getProperty("os.name").toLowerCase().startsWith("linux")) {
            assertTrue(OSUtil.isLinux());
        } else {
            assertFalse(OSUtil.isLinux());
        }
    }

    @Test
    public void testIsMac() {
        if (System.getProperty("os.name").toLowerCase().startsWith("mac")) {
            assertTrue(OSUtil.isMac());
        } else {
            assertFalse(OSUtil.isMac());
        }
    }
}
