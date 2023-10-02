package io.github.linyimin0812.spring.startup.utils;

import io.github.linyimin0812.spring.startup.constant.Constants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author linyimin
 **/
public class ModuleUtilTest {

    private final Path home = Paths.get(System.getProperty(Constants.USER_DIR));

    @Test
    public void testGetModulePaths() {
        List<Path> paths = ModuleUtil.getModulePaths();
        Assertions.assertEquals(1, paths.size());
        String module = paths.get(0).getFileName().toString();
        Assertions.assertEquals("spring-startup-cli", module);
    }

    @Test
    public void compile() {
        Assertions.assertTrue(ModuleUtil.compile(home.getParent()));
    }

    @Test
    public void isMaven() {
        Assertions.assertTrue(ModuleUtil.isMaven(home));
    }

    @Test
    public void isGradle() {
        Assertions.assertFalse(ModuleUtil.isGradle(home));
    }

    @Test
    public void hasMvnW() {
        Assertions.assertFalse(ModuleUtil.hasMvnW(home.getParent()));
        Assertions.assertFalse(ModuleUtil.hasMvnW(home));
    }

    @Test
    public void hasGradleW() {
        Assertions.assertFalse(ModuleUtil.hasGradleW(home));
    }

    @Test
    public void buildWithMaven() {
        Assertions.assertTrue(ModuleUtil.buildWithMaven(home.getParent()));
    }

    @Test
    public void buildWithGradle() {
        Assertions.assertFalse(ModuleUtil.buildWithGradle(home.getParent()));
    }
}