package io.github.linyimin0812.profiler.core.container;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author yiminlin
 **/
public class IocContainerTest {

    @Test
    public void copyFile() throws IOException, URISyntaxException {
        URL srcURL = IocContainerTest.class.getClassLoader().getResource("src/empty.txt");

        Assert.assertNotNull(srcURL);

        Path destPath = Paths.get(srcURL.toURI()).getParent().getParent();

        Assert.assertNotNull(destPath);

        Files.deleteIfExists(Paths.get(destPath.toString(), "empty.txt"));

        Assert.assertFalse(Files.exists(Paths.get(destPath.toString(), "empty.txt")));

        IocContainer.copyFile(srcURL.getPath(), destPath + "/empty.txt");

        Assert.assertTrue(Files.exists(Paths.get(destPath.toString(), "empty.txt")));

    }
}