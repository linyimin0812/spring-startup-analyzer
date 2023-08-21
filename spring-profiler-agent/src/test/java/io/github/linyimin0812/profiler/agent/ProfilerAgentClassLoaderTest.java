package io.github.linyimin0812.profiler.agent;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author linyimin
 **/
public class ProfilerAgentClassLoaderTest {

    private static ProfilerAgentClassLoader classLoader;

    @BeforeAll
    public static void init() {
        URL testJarUrl = ProfilerAgentClassLoaderTest.class.getClassLoader().getResource("spring-profiler-api.jar");
        classLoader = new ProfilerAgentClassLoader(new URL[] {testJarUrl});
    }

    @Test
    public void loadClass() throws ClassNotFoundException {
        assertNotNull(classLoader);

        Class<?> clazz = classLoader.loadClass("io.github.linyimin0812.profiler.api.event.AtEnterEvent", true);

        assertEquals("io.github.linyimin0812.profiler.agent.ProfilerAgentClassLoader", clazz.getClassLoader().getClass().getName());

        clazz = classLoader.loadClass("java.lang.String", true);

        assertNull(clazz.getClassLoader());
    }
}