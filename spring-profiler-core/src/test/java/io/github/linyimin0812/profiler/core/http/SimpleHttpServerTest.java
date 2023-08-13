package io.github.linyimin0812.profiler.core.http;

import io.github.linyimin0812.profiler.common.settings.ProfilerSettings;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * @author linyimin
 **/
public class SimpleHttpServerTest {

    @BeforeClass
    public static void init() {
        SimpleHttpServer.stop();
        Assert.assertFalse(isURLAvailable(SimpleHttpServer.endpoint() + "/hello"));
        SimpleHttpServer.start();
        Assert.assertTrue(isURLAvailable(SimpleHttpServer.endpoint() + "/hello"));
    }

    @Test
    public void start() {

    }

    @Test
    public void getPort() {

        Assert.assertEquals(8065, SimpleHttpServer.getPort());

        URL configurationURL = SimpleHttpServerTest.class.getClassLoader().getResource("spring-startup-analyzer.properties");
        assert configurationURL != null;
        ProfilerSettings.loadProperties(configurationURL.getPath());

        Assert.assertEquals(8066, SimpleHttpServer.getPort());

    }

    @Test
    public void endpoint() {

        ProfilerSettings.clear();

        Assert.assertEquals("http://localhost:8065", SimpleHttpServer.endpoint());

        URL configurationURL = SimpleHttpServerTest.class.getClassLoader().getResource("spring-startup-analyzer.properties");
        assert configurationURL != null;
        ProfilerSettings.loadProperties(configurationURL.getPath());

        Assert.assertEquals("http://localhost:8066", SimpleHttpServer.endpoint());
    }

    @Test
    public void stop() throws InterruptedException {
        // wait for start() test finish
        SimpleHttpServer.stop();
        Assert.assertFalse(isURLAvailable(SimpleHttpServer.endpoint() + "/hello"));
    }

    public static boolean isURLAvailable(String endpoint) {
        try {
            URL url = new URL(endpoint);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(3_000);
            connection.setReadTimeout(3_000);

            return connection.getResponseCode() == HttpURLConnection.HTTP_OK;
        } catch (IOException e) {
            return false;
        }
    }
}