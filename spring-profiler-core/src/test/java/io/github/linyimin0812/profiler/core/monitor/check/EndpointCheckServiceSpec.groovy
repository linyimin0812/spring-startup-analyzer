package io.github.linyimin0812.profiler.core.monitor.check

import com.sun.net.httpserver.HttpServer
import io.github.linyimin0812.profiler.common.settings.ProfilerSettings
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import java.lang.reflect.Field

/**
 * @author linyimin
 * */
@Stepwise
class EndpointCheckServiceSpec extends Specification {

    @Shared
    static HttpServer server;
    @Shared
    EndpointCheckService endpointCheckService = new EndpointCheckService();

    def "test init"() {
        when:
        URL configUrl = EndpointCheckServiceTest.class.getClassLoader().getResource("spring-startup-analyzer.properties");
        assert configUrl != null;
        ProfilerSettings.loadProperties(configUrl.getPath());
        endpointCheckService.init();
        Field healthEndpointsField = endpointCheckService.getClass().getDeclaredField("healthEndpoints");
        healthEndpointsField.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<String> healthEndpoints = (List<String>) healthEndpointsField.get(endpointCheckService);

        then:
        healthEndpoints != null
        healthEndpoints.size() == 1
        healthEndpoints.get(0) == 'http://localhost:12346'
    }


    def "test check after init"() {
        when:
        endpointCheckService.init();

        then:
        AppStatus.initializing == endpointCheckService.check()
    }

    def "test check after start"() {
        when:
        start()

        then:
        AppStatus.running == endpointCheckService.check()
    }

    def "test check after stop"() {
        when:
        stop()

        then:
        AppStatus.initializing == endpointCheckService.check()
    }

    private static void start() {

        int port = 12346;

        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/", httpExchange -> {
                try {
                    httpExchange.sendResponseHeaders(200, 0);
                    httpExchange.getResponseBody().close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
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
