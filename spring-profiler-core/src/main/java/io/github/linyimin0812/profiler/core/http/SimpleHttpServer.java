package io.github.linyimin0812.profiler.core.http;

import io.github.linyimin0812.profiler.common.settings.ProfilerSettings;
import io.github.linyimin0812.profiler.core.container.IocContainer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * @author linyimin
 * @date 2023/04/24 16:25
 **/
public class SimpleHttpServer {

    private static HttpServer server;

    public static void main(String[] args) {
        start();
    }

    public static void start() {
        int serverPort = Integer.parseInt(ProfilerSettings.getProperty("spring-startup-analyzer.admin.http.server.port", "8065"));
        try {
            server = HttpServer.create(new InetSocketAddress(serverPort), 0);
            server.createContext("/", new RootHandler());
            server.setExecutor(null);
            server.start();
            System.out.println("Server listening on port " + serverPort);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static class RootHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();

            String message;

            if ("/".equals(path) || "/help".equals(path)) {
                message = "stop: /stop";
            } else if ("/stop".equals(path)) {
                message = "Agent stop.";
                IocContainer.stop();
            } else {
                message = "Not support operations.";
            }

            exchange.sendResponseHeaders(HTTP_OK, message.length());
            OutputStream os = exchange.getResponseBody();
            os.write(message.getBytes(StandardCharsets.UTF_8));
            os.close();

        }
    }

    public static void stop() {
        if (server == null) {
            return;
        }

        server.stop(30);
    }
}
