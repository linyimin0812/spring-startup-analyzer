package io.github.linyimin0812.profiler.extension.enhance.sample.jvmprofiler;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;


/**
 * @author linyimin
 **/
public class FlameGraphTest {

    private static final Map<String, Integer> TRACE_MAP = new HashMap<>();

    @BeforeClass
    public static void init() throws IOException {
        URL profilerURL = FlameGraphTest.class.getClassLoader().getResource("profiler.txt");
        assert profilerURL != null;

        InputStreamReader input = new InputStreamReader(Files.newInputStream(Paths.get(profilerURL.getPath())));
        try (BufferedReader br = new BufferedReader(input)) {
            for (String line; (line = br.readLine()) != null;) {
                int spaceIndex = line.indexOf(" ");
                if (spaceIndex < 0) {
                    continue;
                }

                String trace = line.substring(0, spaceIndex);
                int ticks = Integer.parseInt(line.substring(spaceIndex + 1));

                TRACE_MAP.put(trace, ticks);
            }
        }
    }

    @Test
    public void parse() throws IOException, URISyntaxException {
        FlameGraph fg = new FlameGraph();

        URL templateURL = FlameGraphTest.class.getClassLoader().getResource("flame-graph.html");
        assert templateURL != null;

        String destPath = Paths.get(templateURL.toURI()).getParent() + "/result-flame-graph.html";

        fg.parse(templateURL.getPath(), destPath, TRACE_MAP);

        Path resultPath = Paths.get(destPath);

        Assert.assertTrue(Files.exists(resultPath));

        Files.deleteIfExists(resultPath);

    }
}