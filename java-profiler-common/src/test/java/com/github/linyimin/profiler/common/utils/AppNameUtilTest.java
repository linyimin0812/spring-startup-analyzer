package com.github.linyimin.profiler.common.utils;

import org.junit.After;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

public class AppNameUtilTest {


    @After
    public void clean() throws NoSuchFieldException, IllegalAccessException {
        Class<AppNameUtil> appNameUtilClass = AppNameUtil.class;

        Field appNameField = appNameUtilClass.getDeclaredField("appName");
        appNameField.setAccessible(true);
        appNameField.set(null, null);
    }


    @Test
    public void testGetAppNameFromProjectName() {

        // 设置系统属性
        System.setProperty("project.name", "test-project");
        System.setProperty("spring.application.name", "test-application");
        System.setProperty("sun.java.command", "TestApplication.jar");

        assertEquals("test-project", AppNameUtil.getAppName());

    }

    @Test
    public void testGetAppNameFromSpringApplicationName() {

        // 设置系统属性
        System.clearProperty("project.name");
        System.setProperty("spring.application.name", "test-application");
        System.setProperty("sun.java.command", "TestApplication.jar");

        assertEquals("test-application", AppNameUtil.getAppName());

    }

    @Test
    public void testGetAppNameFromCommand() {

        // 设置系统属性
        System.clearProperty("project.name");
        System.clearProperty("spring.application.name");
        System.setProperty("sun.java.command", "TestApplication.jar");

        assertEquals("TestApplication", AppNameUtil.getAppName());

    }
}
