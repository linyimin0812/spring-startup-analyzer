package io.github.linyimin0812.spring.startup.utils;

import io.github.linyimin0812.spring.startup.constant.Constants;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static io.github.linyimin0812.spring.startup.constant.Constants.OUT;

/**
 * @author linyimin
 **/
public class ModuleUtil {

    public static List<Path> getModulePaths() {
        return getModulePaths(Paths.get(System.getProperty(Constants.USER_DIR)));
    }

    public static List<Path> getModulePaths(Path home) {
        List<Path> moduleHomes = new ArrayList<>();
        try {
            Files.walkFileTree(home, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attrs) {
                    if (!path.toFile().isDirectory()) {
                        return FileVisitResult.CONTINUE;
                    }
                    if (path.resolve(Constants.POM_XML).toFile().exists() || path.resolve(Constants.BUILD_GRADLE).toFile().exists()) {
                        moduleHomes.add(path);
                        return FileVisitResult.CONTINUE;
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            OUT.printf("Acquire module home from %s failed, error message: %s\n", home, e.getMessage());
            return moduleHomes;
        }
        return moduleHomes;
    }

    public static boolean compile(Path home) {
        if (isMaven(home)) {
            return buildWithMaven(home);
        } else if (isGradle(home)) {
            return buildWithGradle(home);
        } else {
            OUT.println("[ERROR] compile error. only support for maven and gradle");
        }

        return false;
    }

    public static boolean isMaven(Path home) {
        return hasFile(home, Constants.POM_XML);
    }

    public static boolean isGradle(Path home) {
        return hasFile(home, Constants.BUILD_GRADLE);
    }

    public static boolean hasMvnW(Path home) {
        if (OSUtil.isWindows()) {
            return hasFile(home, Constants.WIN_NVMW);
        }

        if (OSUtil.isUnix()) {
            return hasFile(home, Constants.UNIX_MVNW);
        }

        return false;
    }

    public static boolean hasGradleW(Path home) {

        if (OSUtil.isWindows()) {
            return hasFile(home, Constants.WIN_GRADLEW);
        }

        if (OSUtil.isUnix()) {
            return hasFile(home, Constants.UNIX_GRADLEW);
        }

        return false;
    }

    public static boolean buildWithMaven(Path home) {

        String[] commands = new String[0];

        if (hasMvnW(home)) {
            if (OSUtil.isUnix()) {
                commands = new String[] { "./mvnw", "compile" };
            }
            if (OSUtil.isWindows()) {
                commands = new String[] { "./mvnw.cmd", "compile" };
            }
        } else {
            commands = new String[] { "mvn", "compile" };
        }

        ShellUtil.Result result = ShellUtil.execute(commands, true);

        return result.code == 0;

    }

    public static boolean buildWithGradle(Path home) {

        String[] commands = new String[0];

        if (hasGradleW(home)) {
            if (OSUtil.isUnix()) {
                commands = new String[] { "./gradlew", "build", "-x", "test" };
            }
            if (OSUtil.isWindows()) {
                commands = new String[] { "./gradlew.bat", "build", "-x", "test" };
            }
        } else {
            commands = new String[] { "gradle", "build", "-x", "test" };
        }

        ShellUtil.Result result = ShellUtil.execute(commands, true);

        return result.code == 0;
    }

    private static boolean hasFile(Path home, String name) {
        if (Files.notExists(home, LinkOption.NOFOLLOW_LINKS) || !Files.isDirectory(home, LinkOption.NOFOLLOW_LINKS)) {
            return false;
        }

        AtomicReference<Boolean> has = new AtomicReference<>(false);

        try {
            Files.walkFileTree(home, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attrs) {
                    if (!path.toFile().isDirectory()) {
                        return FileVisitResult.CONTINUE;
                    }

                    if (path.resolve(name).toFile().exists()) {
                        has.set(true);
                        return FileVisitResult.TERMINATE;
                    }

                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            return false;
        }

        return has.get();
    }
}
