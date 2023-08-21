package io.github.linyimin0812.profiler.core.container;

import io.github.linyimin0812.profiler.api.EventListener;
import io.github.linyimin0812.profiler.api.Lifecycle;
import io.github.linyimin0812.profiler.core.http.SimpleHttpServer;
import io.github.linyimin0812.profiler.core.http.SimpleHttpServerTest;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author linyimin
 **/
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class IocContainerTest {

    @Test
    void copyFile() throws IOException, URISyntaxException {
        URL srcURL = IocContainerTest.class.getClassLoader().getResource("src/empty.txt");

        assertNotNull(srcURL);

        Path destPath = Paths.get(srcURL.toURI()).getParent().getParent();

        assertNotNull(destPath);

        Path destFilePath = Paths.get(destPath.toString(), "empty.txt");

        Files.deleteIfExists(destFilePath);

        assertFalse(Files.exists(destFilePath));

        IocContainer.copyFile(srcURL.getPath(), destPath + "/empty.txt");

        assertTrue(Files.exists(destFilePath));

    }

    @Test
    @Order(1)
    void start() {
        IocContainer.start();
        assertNotNull(IocContainer.getComponent(LifecycleTest.class));
        assertTrue(SimpleHttpServerTest.isURLAvailable(SimpleHttpServer.endpoint() + "/hello"));
        assertTrue(IocContainer.isStarted());
    }

    @Test
    @Order(2)
    void getComponent() {
        assertNotNull(IocContainer.getComponent(LifecycleTest.class));
        assertNotNull(IocContainer.getComponent(EventListenerTest.class));
    }

    @Test
    @Order(2)
    void getComponents() {
        assertEquals(2, IocContainer.getComponents(Lifecycle.class).size());
        assertEquals(2, IocContainer.getComponents(EventListener.class).size());
    }

    @Test
    @Order(3)
    void stop() {
        assertNotNull(IocContainer.getComponent(LifecycleTest.class));
        try {
            IocContainer.stop();
        } catch (Exception ignored) {

        }

        assertTrue(IocContainer.isStopped());

    }
}