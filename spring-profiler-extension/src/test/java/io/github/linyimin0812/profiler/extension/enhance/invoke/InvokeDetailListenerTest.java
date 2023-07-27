package io.github.linyimin0812.profiler.extension.enhance.invoke;

import io.github.linyimin0812.profiler.common.settings.ProfilerSettings;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.URL;

import static org.junit.Assert.*;

/**
 * @author linyimin
 **/
public class InvokeDetailListenerTest {

    private static final InvokeDetailListener invokeDetailListener = new InvokeDetailListener();

    @BeforeClass
    public static void init() {
        URL configurationURL = InvokeDetailListenerTest.class.getClassLoader().getResource("spring-startup-analyzer.properties");
        assert configurationURL != null;
        ProfilerSettings.loadProperties(configurationURL.getPath());

        invokeDetailListener.start();
    }

    @Test
    public void filter() {
        Assert.assertTrue(invokeDetailListener.filter("java.net.URLClassLoader"));
        Assert.assertFalse(invokeDetailListener.filter("java.lang.String"));
    }

    @Test
    public void testFilter() {
        Assert.assertTrue(invokeDetailListener.filter("findResource", new String[] {"java.lang.String"}));
        Assert.assertFalse(invokeDetailListener.filter("findResource", new String[] {}));
    }

    @Test
    public void onEvent() {
    }

    @Test
    public void listen() {
    }

    @Test
    public void start() {
    }

    @Test
    public void stop() {
    }
}