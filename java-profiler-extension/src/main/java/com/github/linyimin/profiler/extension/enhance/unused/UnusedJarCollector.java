package com.github.linyimin.profiler.extension.enhance.unused;

import ch.qos.logback.classic.Logger;
import com.github.linyimin.profiler.api.Lifecycle;
import com.github.linyimin.profiler.common.logger.LogFactory;
import com.github.linyimin.profiler.common.markdown.MarkdownWriter;
import com.github.linyimin.profiler.common.instruction.InstrumentationHolder;
import org.kohsuke.MetaInfServices;

import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.*;

/**
 * @author linyimin
 * @date 2023/05/01 12:18
 * @description Collect unused Jar files under the classloader
 **/
@MetaInfServices(Lifecycle.class)
public class UnusedJarCollector implements Lifecycle {

    private static final Logger logger = LogFactory.getStartupLogger();

    private Map<ClassLoader, Set<String>> collect() {

        Map<ClassLoader, Set<String>> usedJarMap = new HashMap<>();

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

        Map<ClassLoader, Set<String>> unusedJarMap = new HashMap<>();

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
        return unusedJarMap;
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
        logger.info("=======================UnusedJarCollector start=======================");
    }

    @Override
    public void stop() {
        logger.info("=======================UnusedJarCollector stop=======================");
        Map<ClassLoader, Set<String>> map =collect();
        StringBuilder unusedJar = new StringBuilder("<details open>\n")
                .append("<summary><h1 style='display: inline'>Unused JARs</h1></summary>\n")
                .append("<hr/>\n");

        for (Map.Entry<ClassLoader, Set<String>> entry : map.entrySet()) {
            unusedJar.append("<details>\n")
                    .append("<summary style='margin-left: 12px'><h2 style='display: inline'>").append(entry.getKey()).append("</h2></summary>\n");
            unusedJar.append("<ul>\n");
            for (String url : entry.getValue()) {
                unusedJar.append("<li>").append(url).append("</li>\n");
            }
            unusedJar.append("</ul>\n").append("</details>");
        }
        unusedJar.append("</details>\n");
        MarkdownWriter.write(Integer.MAX_VALUE, unusedJar.toString());
    }
}
