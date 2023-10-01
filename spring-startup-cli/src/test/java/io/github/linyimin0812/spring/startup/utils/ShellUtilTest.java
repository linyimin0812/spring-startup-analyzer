package io.github.linyimin0812.spring.startup.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author linyimin
 **/
class ShellUtilTest {

    @Test
    void execute() {
        if (OSUtil.isUnix()) {
            ShellUtil.Result result = ShellUtil.execute(new String[] { "ls", "-al" });
            assertEquals(0, result.code);
        } else if (OSUtil.isWindows()) {
            ShellUtil.Result result = ShellUtil.execute(new String[] { "dir" });
            assertEquals(0, result.code);
        }

    }

    @Test
    void testExecute() {
        if (OSUtil.isUnix()) {
            ShellUtil.Result result = ShellUtil.execute(new String[] { "ls", "-al" }, true);
            assertEquals(0, result.code);
        } else if (OSUtil.isWindows()) {
            ShellUtil.Result result = ShellUtil.execute(new String[] { "dir" }, true);
            assertEquals(0, result.code);
        }
    }
}