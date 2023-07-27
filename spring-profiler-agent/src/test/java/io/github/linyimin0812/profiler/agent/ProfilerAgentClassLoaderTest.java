package io.github.linyimin0812.profiler.agent;

import org.junit.Assert;
import org.junit.BeforeClass;

import java.net.URL;

/**
 * @author banzhe
 **/
public class ProfilerAgentClassLoaderTest {

    private static ProfilerAgentClassLoader classLoader;

    @BeforeClass
    public static void init() {
        URL testJarUrl = ProfilerAgentClassLoaderTest.class.getClassLoader().getResource("spring-profiler-api.jar");
        classLoader = new ProfilerAgentClassLoader(new URL[] {testJarUrl});
    }

    @org.junit.Test
    public void loadClass() throws ClassNotFoundException {
        Assert.assertNotNull(classLoader);

        Class<?> clazz = classLoader.loadClass("io.github.linyimin0812.profiler.api.event.AtEnterEvent", true);

        Assert.assertEquals("io.github.linyimin0812.profiler.agent.ProfilerAgentClassLoader", clazz.getClassLoader().getClass().getName());

        clazz = classLoader.loadClass("java.lang.String", true);

        Assert.assertNull(clazz.getClassLoader());
    }
}