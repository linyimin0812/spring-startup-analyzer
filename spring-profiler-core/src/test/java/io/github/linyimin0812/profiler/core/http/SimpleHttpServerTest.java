package io.github.linyimin0812.profiler.core.http;

import io.github.linyimin0812.profiler.common.settings.ProfilerSettings;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author linyimin
 **/
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SimpleHttpServerTest {

    @Test
    @Order(0)
    void start() {
        SimpleHttpServer.stop();
        assertFalse(isURLAvailable(SimpleHttpServer.endpoint() + "/hello"));
        SimpleHttpServer.start();
        assertTrue(isURLAvailable(SimpleHttpServer.endpoint() + "/hello"));
    }

    @Test
    @Order(3)
    void getPort() {

        ProfilerSettings.clear();

        assertEquals(8065, SimpleHttpServer.getPort());

        URL configurationURL = SimpleHttpServerTest.class.getClassLoader().getResource("spring-startup-analyzer.properties");
        assert configurationURL != null;
        ProfilerSettings.loadProperties(configurationURL.getPath());

        assertEquals(8066, SimpleHttpServer.getPort());

    }

    @Test
    @Order(3)
    void endpoint() {

        ProfilerSettings.clear();

        assertEquals("http://localhost:8065", SimpleHttpServer.endpoint());

        URL configurationURL = SimpleHttpServerTest.class.getClassLoader().getResource("spring-startup-analyzer.properties");
        assert configurationURL != null;
        ProfilerSettings.loadProperties(configurationURL.getPath());

        assertEquals("http://localhost:8066", SimpleHttpServer.endpoint());
    }

    @Test
    @Order(2)
    void stop() {
        assertTrue(isURLAvailable(SimpleHttpServer.endpoint() + "/hello"));
        SimpleHttpServer.stop();
        assertFalse(isURLAvailable(SimpleHttpServer.endpoint() + "/hello"));
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