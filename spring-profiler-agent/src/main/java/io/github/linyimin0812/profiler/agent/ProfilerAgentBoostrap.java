package io.github.linyimin0812.profiler.agent;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author linyimin
 **/
public class ProfilerAgentBoostrap {

    private static final String BRIDGE_JAR = "spring-profiler-bridge.jar";

    private static final Logger logger = Logger.getLogger(ProfilerAgentBoostrap.class.getSimpleName());
    private static final String LIB_HOME = getLibHome();
    private static final String EXTENSION_HOME = LIB_HOME + "extension" + File.separator;

    public static void premain(String args, Instrumentation instrumentation) {

        logger.info("command args: " + args);

        // bridge.jar
        File spyJarFile = new File(LIB_HOME + BRIDGE_JAR);
        if (!spyJarFile.exists()) {
            logger.info("Spy jar file does not exist: " + spyJarFile);
            return;
        }
        // load agent-spy.jar
        try {
            instrumentation.appendToBootstrapClassLoaderSearch(new JarFile(spyJarFile));
        } catch (IOException ignore) {

        }

        // load agent-core.jar
        final ClassLoader agentLoader;
        try {
            agentLoader = createAgentClassLoader();
            Class<?> transFormer = agentLoader.loadClass("io.github.linyimin0812.profiler.core.enhance.ProfilerClassFileTransformer");
            Constructor<?> constructor = transFormer.getConstructor(Instrumentation.class);
            Method retransform = transFormer.getDeclaredMethod("retransformLoadedClass");
            Object instance = constructor.newInstance(instrumentation);

            instrumentation.addTransformer((ClassFileTransformer) instance, true);

            retransform.invoke(instance);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception: " + args);
            throw new RuntimeException(e);
        }
    }

    private static ClassLoader createAgentClassLoader() throws MalformedURLException {

        List<URL> urlList = new ArrayList<>();

        // 加载lib下的jar包
        urlList.addAll(getJars(LIB_HOME));
        urlList.addAll(getJars(EXTENSION_HOME));

        logger.info(urlList.toString());

        return new ProfilerAgentClassLoader(urlList.toArray(new URL[0]));
    }

    private static List<URL> getJars(String path) throws MalformedURLException {

        List<URL> urlList = new ArrayList<>();

        // 加载lib下的jar包
        File folder = new File(path);

        if (!folder.exists()) {
            throw new IllegalStateException(path + " is not exit.");
        }

        File[] files = folder.listFiles(file -> !file.getName().contains(BRIDGE_JAR) && file.isFile() && file.getName().endsWith("jar"));

        if (files == null) {
            throw new IllegalStateException(path + " does not contain any jar files.");
        }

        for (File file : files) {
            urlList.add(file.toURI().toURL());
        }

        return urlList;

    }

    private static String getLibHome() {

        // -javaagent:C:\runner\spring-startup-analyzer\lib\spring-profiler-agent.jar


        String currentFilePath = ProfilerAgentBoostrap.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath();

        File file = new File(currentFilePath);

        if (!file.exists()) {
            return System.getProperty("user.home") + File.separator + "spring-startup-analyzer" + File.separator;
        }

        return file.getParent() + File.separator;

    }

    public static void main(String[] args) throws IOException {
        String text = "-javaagent:C:\\runner\\spring-startup-analyzer\\lib\\spring-profiler-agent.jar";

        int index= text.indexOf(":");
        System.out.println(text.substring(0, index));
        System.out.println(text.substring(index + 1));
    }

}
