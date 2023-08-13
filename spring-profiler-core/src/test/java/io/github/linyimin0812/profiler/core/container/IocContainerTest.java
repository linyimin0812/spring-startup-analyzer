package io.github.linyimin0812.profiler.core.container;

import io.github.linyimin0812.profiler.api.EventListener;
import io.github.linyimin0812.profiler.api.Lifecycle;
import io.github.linyimin0812.profiler.core.http.SimpleHttpServer;
import io.github.linyimin0812.profiler.core.http.SimpleHttpServerTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author linyimin
 **/
public class IocContainerTest {

    @BeforeClass
    public static void init() {
        IocContainer.start();
    }

    @Test
    public void copyFile() throws IOException, URISyntaxException {
        URL srcURL = IocContainerTest.class.getClassLoader().getResource("src/empty.txt");

        Assert.assertNotNull(srcURL);

        Path destPath = Paths.get(srcURL.toURI()).getParent().getParent();

        Assert.assertNotNull(destPath);

        Path destFilePath = Paths.get(destPath.toString(), "empty.txt");

        Files.deleteIfExists(destFilePath);

        Assert.assertFalse(Files.exists(destFilePath));

        IocContainer.copyFile(srcURL.getPath(), destPath + "/empty.txt");

        Assert.assertTrue(Files.exists(destFilePath));

    }

    @Test
    public void start() {
        Assert.assertNotNull(IocContainer.getComponent(LifecycleTest.class));
        Assert.assertTrue(SimpleHttpServerTest.isURLAvailable(SimpleHttpServer.endpoint() + "/hello"));
    }

    @Test(expected = RuntimeException.class)
    public void stop() {
        if (!IocContainer.isStopped()) {
            Assert.assertNotNull(IocContainer.getComponent(LifecycleTest.class));
            IocContainer.stop();
        } else {
            throw new RuntimeException();
        }

    }

    @Test
    public void getComponent() {
        if (!IocContainer.isStarted()) {
            Assert.assertNull(IocContainer.getComponent(LifecycleTest.class));
            Assert.assertNull(IocContainer.getComponent(EventListenerTest.class));
        }

        if (IocContainer.isStarted() && !IocContainer.isStopped()) {
            Assert.assertNotNull(IocContainer.getComponent(LifecycleTest.class));
            Assert.assertNotNull(IocContainer.getComponent(EventListenerTest.class));
        }
    }

    @Test
    public void getComponents() {
        if (!IocContainer.isStarted()) {
            Assert.assertEquals(0, IocContainer.getComponents(Lifecycle.class).size());
            Assert.assertEquals(0, IocContainer.getComponents(EventListener.class).size());
        }

        if (IocContainer.isStarted() && !IocContainer.isStopped()) {
            Assert.assertEquals(2, IocContainer.getComponents(Lifecycle.class).size());
            Assert.assertEquals(2, IocContainer.getComponents(EventListener.class).size());
        }
    }
}