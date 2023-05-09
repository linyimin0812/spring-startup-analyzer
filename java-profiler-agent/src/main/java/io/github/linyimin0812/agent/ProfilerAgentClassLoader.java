package io.github.linyimin0812.agent;

import java.net.URL;
import java.net.URLClassLoader;

public class ProfilerAgentClassLoader extends URLClassLoader {

    private static final String BRIDGE_CLASS = "io.github.linyimin0812.Bridge";

    static {
        ClassLoader.registerAsParallelCapable();
    }

    public ProfilerAgentClassLoader(URL[] urls) {
        super(urls, ClassLoader.getSystemClassLoader().getParent());
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {

        final Class<?> loadedClass = findLoadedClass(name);
        if (loadedClass != null) {
            return loadedClass;
        }

        // 优先从parent加载系统类，避免抛出ClassNotFoundException
        if (name != null && (name.startsWith("java.") || name.startsWith("sun.") || BRIDGE_CLASS.equals(name))) {
            return super.loadClass(name, resolve);
        }

        try {
            Class<?> aClass = findClass(name);
            if (resolve) {
                resolveClass(aClass);
            }

            return aClass;
        } catch (Exception ignore) {

        }

        return super.loadClass(name, resolve);
    }
}
