package io.github.linyimin0812.profiler.extension.enhance.sample.jvmprofiler

import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author linyimin
 * */
class FlameGraphSpec extends Specification {

    @Shared
    final Map<String, Integer> TRACE_MAP = new HashMap<>()

    def setup() {
        URL profilerURL = FlameGraphSpec.class.getClassLoader().getResource("profiler.txt")
        assert profilerURL != null

        InputStreamReader input = new InputStreamReader(Files.newInputStream(Paths.get(profilerURL.getPath())));
        try (BufferedReader br = new BufferedReader(input)) {
            for (String line; (line = br.readLine()) != null;) {
                int spaceIndex = line.indexOf(" ")
                if (spaceIndex < 0) {
                    continue
                }

                String trace = line.substring(0, spaceIndex)
                int ticks = Integer.parseInt(line.substring(spaceIndex + 1))

                TRACE_MAP.put(trace, ticks)
            }
        }
    }

    def "test parse"() {

        given:
        FlameGraph fg = new FlameGraph()
        URL templateURL = FlameGraphSpec.class.getClassLoader().getResource("flame-graph.html")
        String destPath = Paths.get(templateURL.toURI()).getParent().toString() + "/result-flame-graph.html"

        when:
        fg.parse(templateURL.getPath(), destPath, TRACE_MAP)
        Path resultPath = Paths.get(destPath)

        then:
        Files.exists(resultPath)

        cleanup:
        Files.deleteIfExists(resultPath)
    }
}
