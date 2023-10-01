package io.github.linyimin0812.spring.startup.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author linyimin
 **/
class GitUtilTest {

    @Test
    void isGit() {
        Assertions.assertTrue(GitUtil.isGit());
    }

    @Test
    void currentBranch() {
        Assertions.assertNotNull(GitUtil.currentBranch());
    }
}