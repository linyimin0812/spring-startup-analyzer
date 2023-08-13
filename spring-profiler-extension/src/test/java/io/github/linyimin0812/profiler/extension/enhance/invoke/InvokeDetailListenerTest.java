package io.github.linyimin0812.profiler.extension.enhance.invoke;

import io.github.linyimin0812.profiler.common.settings.ProfilerSettings;
import io.github.linyimin0812.profiler.common.ui.MethodInvokeDetail;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author linyimin
 **/
public class InvokeDetailListenerTest {

    private static final InvokeDetailListener invokeDetailListener = new InvokeDetailListener();

    @Before
    public void init() {
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

//        AtEnterEvent atEnterEvent = new AtEnterEvent(1, 1, URLClassLoader.class, new URLClassLoader(new URL[] {}), "findResource", "findResource", );

    }

    @Test
    public void listen() {
        assertEquals(2, invokeDetailListener.listen().size());
    }

    @Test
    public void start() throws NoSuchFieldException, IllegalAccessException {
        Field field = invokeDetailListener.getClass().getDeclaredField("methodQualifiers");
        field.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<String> methodQualifiers = (List<String>) field.get(invokeDetailListener);

        assertEquals(1, methodQualifiers.size());
        assertEquals("java.net.URLClassLoader.findResource(java.lang.String)", methodQualifiers.get(0));
    }

    @Test
    public void stop() throws NoSuchFieldException, IllegalAccessException {
        invokeDetailListener.stop();
        Field field = invokeDetailListener.getClass().getDeclaredField("methodQualifiers");
        field.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<String> methodQualifiers = (List<String>) field.get(invokeDetailListener);

        assertTrue(methodQualifiers.isEmpty());

        field = invokeDetailListener.getClass().getDeclaredField("INVOKE_DETAIL_MAP");
        field.setAccessible(true);

        @SuppressWarnings("unchecked")
        Map<String, MethodInvokeDetail> map = (Map<String, MethodInvokeDetail>) field.get(invokeDetailListener);
        assertTrue(map.isEmpty());
    }
}