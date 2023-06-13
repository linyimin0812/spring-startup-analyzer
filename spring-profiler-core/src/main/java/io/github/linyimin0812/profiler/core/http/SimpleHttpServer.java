package io.github.linyimin0812.profiler.core.http;

import io.github.linyimin0812.profiler.common.settings.ProfilerSettings;
import io.github.linyimin0812.profiler.common.utils.NameUtil;
import io.github.linyimin0812.profiler.core.container.IocContainer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * @author linyimin
 **/
public class SimpleHttpServer {

    private static HttpServer server;

    public static void main(String[] args) {
        start();
    }

    public static void start() {
        try {
            server = HttpServer.create(new InetSocketAddress(getPort()), 0);
            server.createContext("/", new RootHandler());
            server.setExecutor(null);
            server.start();
            System.out.println("Server listening on port " + getPort());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static class RootHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String url = exchange.getRequestURI().getPath();

            byte[] content;

            if ("/stop".equals(url)) {
                content = "Agent stop.".getBytes(StandardCharsets.UTF_8);
                IocContainer.stop();
            } if (url.endsWith("flame-graph.html")) {
                Path path = Paths.get(NameUtil.getOutputPath(), url);
                content = Files.readAllBytes(path);
            } else {
                Path path = Paths.get(NameUtil.getOutputPath(), NameUtil.getAnalysisHtmlName());
                content = Files.readAllBytes(path);
            }

            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(HTTP_OK, content.length);
            OutputStream os = exchange.getResponseBody();
            os.write(content);
            os.close();

        }
    }

    public static int getPort() {
        return Integer.parseInt(ProfilerSettings.getProperty("spring-startup-analyzer.admin.http.server.port", "8065"));
    }

    public static String endpoint() {
        return "http://localhost:" + getPort();
    }
}
