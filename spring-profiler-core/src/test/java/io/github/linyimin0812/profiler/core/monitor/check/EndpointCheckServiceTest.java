package io.github.linyimin0812.profiler.core.monitor.check;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import io.github.linyimin0812.profiler.common.settings.ProfilerSettings;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.List;

/**
 * @author linyimin
 **/
class EndpointCheckServiceTest {

    private static HttpServer server;
    private static EndpointCheckService endpointCheckService;

    @BeforeAll
    static void initEnv() {
        endpointCheckService = new EndpointCheckService();
    }

    @Test
    @Order(0)
    void init() throws NoSuchFieldException, IllegalAccessException {
        URL configUrl = EndpointCheckServiceTest.class.getClassLoader().getResource("spring-startup-analyzer.properties");
        assert configUrl != null;
        ProfilerSettings.loadProperties(configUrl.getPath());
        endpointCheckService.init();
        Field healthEndpointsField = endpointCheckService.getClass().getDeclaredField("healthEndpoints");
        healthEndpointsField.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<String> healthEndpoints = (List<String>) healthEndpointsField.get(endpointCheckService);

        Assertions.assertNotNull(healthEndpoints);
        Assertions.assertEquals(1, healthEndpoints.size());
        Assertions.assertEquals("http://localhost:12346", healthEndpoints.get(0));
    }

    @Test
    @Order(1)
    void check() {
        Assertions.assertEquals(AppStatus.initializing, endpointCheckService.check());

        start();
        Assertions.assertEquals(AppStatus.running, endpointCheckService.check());

        stop();

        Assertions.assertEquals(AppStatus.initializing, endpointCheckService.check());

    }

    private static void start() {

        int port = 12346;

        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/", new HttpHandler() {
                @Override
                public void handle(HttpExchange httpExchange) throws IOException {
                    try {
                        httpExchange.sendResponseHeaders(200, 0);
                        httpExchange.getResponseBody().close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            server.setExecutor(null);
            server.start();
            System.out.println("Server listening on port " + port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void stop() {
        server.stop(0);
    }
}