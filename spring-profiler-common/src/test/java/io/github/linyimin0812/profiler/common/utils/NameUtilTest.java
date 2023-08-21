package io.github.linyimin0812.profiler.common.utils;

import org.junit.jupiter.api.*;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NameUtilTest {

    @AfterEach
    public void clean() throws NoSuchFieldException, IllegalAccessException {
        Class<NameUtil> appNameUtilClass = NameUtil.class;

        Field appNameField = appNameUtilClass.getDeclaredField("appName");
        appNameField.setAccessible(true);
        appNameField.set(null, null);
    }

    @Test
    @Order(1)
    public void testGetAppNameFromProjectName() {

        // 设置系统属性
        System.setProperty("project.name", "test-project");
        System.setProperty("spring.application.name", "test-application");
        System.setProperty("sun.java.command", "TestApplication.jar");

        assertEquals("test-project", NameUtil.getAppName());

    }

    @Test
    @Order(2)
    public void testGetAppNameFromSpringApplicationName() {

        // 设置系统属性
        System.clearProperty("project.name");
        System.setProperty("spring.application.name", "test-application");
        System.setProperty("sun.java.command", "TestApplication.jar");

        assertEquals("test-application", NameUtil.getAppName());

    }

    @Test
    @Order(3)
    public void testGetAppNameFromCommand() {

        // 设置系统属性
        System.clearProperty("project.name");
        System.clearProperty("spring.application.name");
        System.setProperty("sun.java.command", "TestApplication.jar");

        assertEquals("TestApplication", NameUtil.getAppName());

    }
}
