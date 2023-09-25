package io.github.linyimin0812.spring.startup.recompile;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * @author linyimin
 **/
public class ModulePath {

    public static List<Path> get() {
        return get(Paths.get(System.getProperty(Constants.USER_DIR)));
    }

    public static List<Path> get(Path home) {
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
            System.out.printf("Acquire module home from %s failed, error message: %s\n", home, e.getMessage());
            return moduleHomes;
        }
        return moduleHomes;
    }
}
