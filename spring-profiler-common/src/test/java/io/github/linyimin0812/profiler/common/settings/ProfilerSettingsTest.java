package io.github.linyimin0812.profiler.common.settings;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.URL;

/**
 * @author linyimin
 * @date 2023/07/27 16:15
 **/
public class ProfilerSettingsTest {


    @BeforeClass
    public static void init() {
        URL configurationURL = ProfilerSettingsTest.class.getClassLoader().getResource("spring-startup-analyzer.properties");
        assert configurationURL != null;
        ProfilerSettings.loadProperties(configurationURL.getPath());
    }

    @Test
    public void testGetProperty() {
        Assert.assertNull(ProfilerSettings.getProperty("key"));
        Assert.assertEquals("default", ProfilerSettings.getProperty("key", "default"));
        Assert.assertEquals("testValue3", ProfilerSettings.getProperty("testKey3"));

        Assert.assertNotEquals("/xxxxxx/", ProfilerSettings.getProperty("user.home"));
        Assert.assertNotEquals("/xxxxxx/", ProfilerSettings.getProperty("user.home", "/xxxxxx/"));

    }

    @Test
    public void contains() {
        Assert.assertTrue(ProfilerSettings.contains("testKey2"));
        Assert.assertFalse(ProfilerSettings.contains("key"));
    }

    @Test
    public void isNotBlank() {
        Assert.assertTrue(ProfilerSettings.isNotBlank("test"));
        Assert.assertFalse(ProfilerSettings.isNotBlank(""));
    }

    @Test
    public void loadProperties() {
        init();
    }
}