package io.github.linyimin0812.spring.startup.recompile;

import io.github.linyimin0812.spring.startup.constant.Constants;
import io.github.linyimin0812.spring.startup.utils.ModuleUtil;
import io.github.linyimin0812.spring.startup.utils.StringUtil;
import io.methvin.watcher.DirectoryWatcher;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author linyimin
 **/
public class ModifiedFileWatcher {

    private final DirectoryWatcher watcher;

    public boolean running = false;

    public ModifiedFileWatcher(ModifiedFileProcessor processor) throws IOException {
        this(System.getProperty(Constants.USER_DIR), processor);
    }

    /**
     * Creates a WatchService and registers the given directory
     */
    public ModifiedFileWatcher(String dir, ModifiedFileProcessor processor) throws IOException {

        Path path = Paths.get(dir);

        List<Path> moduleHomes = ModuleUtil.getModulePaths(path);

        List<Path> moduleSourceDirs = moduleHomes.stream().map(moduleHome -> moduleHome.resolve(Constants.SOURCE_DIR)).filter(Files::exists).collect(Collectors.toList());

        this.watcher = DirectoryWatcher.builder()
                .paths(moduleSourceDirs)
                .listener(processor::onEvent)
                .build();

        int longest = moduleHomes.stream().map(Path::toString).map(String::length).max(Integer::compareTo).orElse(0) + 32;

        for (Path moduleHome : moduleHomes) {
            System.out.format("[INFO] %s WATCHING\n", StringUtil.rightPad(moduleHome.toString() + Constants.SPACE, longest, "."));
        }

        new Thread(watcher::watch).start();

        running = true;
    }

    public void close() throws IOException {
        running = false;
        this.watcher.close();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        ModifiedFileWatcher recompileFileWatcher = new ModifiedFileWatcher("/Users/banzhe/IdeaProjects/project/spring-boot-async-bean-demo/", new ModifiedFileProcessor());

        Thread.sleep(20 * 1000);

        recompileFileWatcher.close();
    }
}
