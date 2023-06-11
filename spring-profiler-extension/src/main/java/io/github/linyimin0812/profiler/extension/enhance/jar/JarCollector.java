package io.github.linyimin0812.profiler.extension.enhance.jar;

import io.github.linyimin0812.profiler.api.Lifecycle;
import io.github.linyimin0812.profiler.common.logger.LogFactory;
import io.github.linyimin0812.profiler.common.instruction.InstrumentationHolder;
import io.github.linyimin0812.profiler.common.ui.StartupVO;
import io.github.linyimin0812.profiler.common.ui.Statistics;
import org.kohsuke.MetaInfServices;
import org.slf4j.Logger;

import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.*;

/**
 * @author linyimin
 * @date 2023/05/01 12:18
 * @description Collect Jar files under the classloader
 **/
@MetaInfServices(Lifecycle.class)
public class JarCollector implements Lifecycle {

    private static final Logger logger = LogFactory.getStartupLogger();

    private Map<ClassLoader, Set<String>> usedJarMap;
    private Map<ClassLoader, Set<String>> unusedJarMap;

    private void collect() {

        usedJarMap = new HashMap<>();
        unusedJarMap = new HashMap<>();

        Instrumentation instrumentation = InstrumentationHolder.getInstrumentation();

        for (Class<?> loadedClass : instrumentation.getAllLoadedClasses()) {
            ClassLoader loader = loadedClass.getClassLoader();
            if (loader == null
                    || loader.toString().contains("ExtClassLoader")
                    || loader.toString().contains("ProfilerAgentClassLoader")) {
                continue;
            }
            ProtectionDomain protectionDomain = loadedClass.getProtectionDomain();
            CodeSource codeSource = protectionDomain.getCodeSource();
            if (codeSource == null) {
                continue;
            }
            URL location = codeSource.getLocation();
            if (location == null) {
                continue;
            }
            Set<String> urls = usedJarMap.computeIfAbsent(loader, k -> new HashSet<>());
            urls.add(location.toString());
        }


        for (Map.Entry<ClassLoader, Set<String>> entry : usedJarMap.entrySet()) {
            ClassLoader loader = entry.getKey();
            Set<String> usedUrls = entry.getValue();

            Set<String> unusedUrls = new HashSet<>();

            List<String> allUrls = getClassLoaderUrls(loader);

            for (String url : allUrls) {
                if (usedUrls.contains(url)) {
                    continue;
                }
                unusedUrls.add(url);
            }
            if (!unusedUrls.isEmpty()) {
                unusedJarMap.put(loader, unusedUrls);
            }
        }
    }

    private List<String> getClassLoaderUrls(ClassLoader loader) {
        List<String> urls = new ArrayList<>();
        if (loader instanceof URLClassLoader) {
            URL[] loaderUrls = ((URLClassLoader) loader).getURLs();
            if (loaderUrls == null) {
                return urls;
            }

            for (URL url : loaderUrls) {
                urls.add(url.toString());
            }
        }
        return urls;
    }

    @Override
    public void start() {
        logger.info("=======================JarCollector start=======================");
    }

    @Override
    public void stop() {
        logger.info("=======================JarCollector stop=======================");

        collect();

        reportJarStatistics();

        reportUnusedJarDetails();
    }

    private void reportJarStatistics() {

        long usedCount = usedJarMap.values().stream().mapToLong(Collection::size).sum();
        long unusedCount = unusedJarMap.values().stream().mapToLong(Collection::size).sum();

        StartupVO.addStatistics(new Statistics("Used/Total Jars", String.format("%s/%s", usedCount, usedCount + unusedCount)));
        StartupVO.addStatistics(new Statistics("Unused/Total Jars", String.format("%s/%s", unusedCount, usedCount + unusedCount)));
        StartupVO.addStatistics(new Statistics("ClassLoader Count", String.valueOf(usedJarMap.size())));
    }

    private void reportUnusedJarDetails() {

        StringBuilder unusedJar = new StringBuilder("<details open>\n")
                .append("<summary><h1 style='display: inline'>Unused JARs</h1></summary>\n")
                .append("<hr/>\n");

        unusedJar.append("<table>\n")
                .append("<tr>\n")
                .append("<th>ClassLoader</th>\n")
                .append("<th>Unused Jar Count</th>\n")
                .append("</tr>");

        List<Map.Entry<ClassLoader, Set<String>>> entryList = new ArrayList<>(unusedJarMap.entrySet());
        entryList.sort((o1, o2) -> o2.getValue().size() - o1.getValue().size());

        for (Map.Entry<ClassLoader, Set<String>> entry : entryList) {

            StringBuilder unusedJarUl = new StringBuilder("<details>\n")
                    .append("<summary>").append(entry.getKey()).append("</summary>\n")
                    .append("<ul>\n");

            for (String url : entry.getValue()) {
                unusedJarUl.append("<li>").append(url).append("</li>\n");
            }
            unusedJarUl.append("</ul>\n").append("</details>\n");

            unusedJar.append("<tr>\n")
                    .append(String.format("<td>%s</td>\n", unusedJarUl))
                    .append(String.format("<td style='text-align: center;'>%s</td>\n", entry.getValue().size()))
                    .append("</tr>\n");
        }
        unusedJar.append("</table>").append("</details>\n").append("<hr/>\n");
    }
}
