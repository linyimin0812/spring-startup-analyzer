package io.github.linyimin0812.profiler.common.utils;

import io.github.linyimin0812.profiler.common.logger.LogFactory;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * @author linyimin
 * @date 2023/05/08 10:32
 * @description Get the package name of the class containing main method
 **/
public class MainClassUtil {

    private static final Logger logger = LogFactory.getStartupLogger();

    private static final Set<String> packages = new HashSet<>();

    public static Set<String> getPackages() {
        return packages;
    }

    public static void resolveMainClassPackage(List<URL> manifestPath) {

        // 1. get from the MANIFEST.MF file
        Set<String> manifestPackages = resolvePackageFromManifest(manifestPath);

        if (!manifestPackages.isEmpty()) {
            packages.addAll(manifestPackages);
            return;
        }

        // 2. get from "sun.java.command" property
        Set<String> commandPackages = resolvePackageFromCommand();
        packages.addAll(commandPackages);

    }

    private static Set<String> resolvePackageFromManifest(List<URL> manifestPaths) {

        Set<String> startClassPackages = new HashSet<>();

        for (URL url : manifestPaths) {
            try (InputStream inputStream = url.openStream()) {
                Manifest manifest = new Manifest(inputStream);
                Attributes attributes = manifest.getMainAttributes();
                String startClass = attributes.getValue("Start-Class");

                if (startClass == null || startClass.length() == 0) {
                    continue;
                }

                if (startClass.contains(".")) {
                    startClassPackages.add(startClass.substring(0, startClass.lastIndexOf(".")));
                }

            } catch (IOException e) {
                logger.error("parse {} error.", url.getPath(), e);
            }
        }

        return startClassPackages;
    }

    private static Set<String> resolvePackageFromCommand() {

        Set<String> mainClassPackages = new HashSet<>();

        String command = System.getProperty("sun.java.command");

        if (command == null || command.isEmpty()) {
            return mainClassPackages;
        }
        command = command.split("\\s")[0];
        String separator = File.separator;
        if (command.contains(separator)) {
            return mainClassPackages;
        }
        if (command.contains(".")) {
            mainClassPackages.add(command.substring(0, command.lastIndexOf(".")));
        }

        return mainClassPackages;
    }
}
