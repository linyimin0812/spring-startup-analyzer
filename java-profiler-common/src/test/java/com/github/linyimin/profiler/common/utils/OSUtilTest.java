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

    @Test
    public void testIsArm32() {
        if ("arm_32".equals(OSUtil.arch)) {
            assertTrue(OSUtil.isArm32());
        } else {
            assertFalse(OSUtil.isArm32());
        }
    }

    @Test
    public void testIsArm64() {
        if ("aarch_64".equals(OSUtil.arch)) {
            assertTrue(OSUtil.isArm64());
        } else {
            assertFalse(OSUtil.isArm64());
        }
    }
}
