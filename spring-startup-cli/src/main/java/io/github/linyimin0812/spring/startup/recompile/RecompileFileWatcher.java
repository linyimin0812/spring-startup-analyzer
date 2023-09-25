package io.github.linyimin0812.spring.startup.recompile;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.*;

/**
 * The original comes from <a href="https://docs.oracle.com/javase/tutorial/displayCode.html?code=https://docs.oracle.com/javase/tutorial/essential/io/examples/WatchDir.java">WatchDir</a>
 * @author linyimin
 **/
public class RecompileFileWatcher {

    private final WatchService watcher;
    private final Map<WatchKey, Path> keys;

    private final RecompileFileProcessor processor;

    private boolean running = true;

    /**
     * Register the given directory, and all its subdirectories, with the
     * WatchService.
     */
    private void registerAll(final Path start) throws IOException {

        if (!start.toFile().exists()) {
            return;
        }

        // register directory and subdirectories
        Files.walkFileTree(start, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {

                WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
                keys.put(key, dir);

                return FileVisitResult.CONTINUE;
            }
        });
    }

    public RecompileFileWatcher(RecompileFileProcessor processor) throws IOException {
        this(System.getProperty(Constants.USER_DIR), processor);
    }

    /**
     * Creates a WatchService and registers the given directory
     */
    public RecompileFileWatcher(String dir, RecompileFileProcessor processor) throws IOException {

        Path path = Paths.get(dir);

        this.keys = new HashMap<>();
        this.processor = processor;
        this.watcher = FileSystems.getDefault().newWatchService();


        List<Path> moduleHomes = ModulePath.get(path);

        for (Path moduleHome : moduleHomes) {
            System.out.format("Watching module %s ...\n", moduleHome);
            registerAll(moduleHome.resolve(Constants.SOURCE_DIR));
        }

        new Thread(this::processEvents).start();
    }

    /**
     * Process all events for keys queued to the watcher
     */
    private void processEvents() {

        while (running) {

            // wait for key to be signalled
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException | ClosedWatchServiceException ignored) {
                System.out.println("[INFO] File-Watcher closed");
                return;
            }

            Path dir = keys.get(key);
            if (dir == null) {
                System.err.println("WatchKey not recognized!!");
                continue;
            }

            for (WatchEvent<?> event: key.pollEvents()) {

                WatchEvent.Kind<?> kind = event.kind();

                // TBD - provide example of how OVERFLOW event is handled
                if (kind == OVERFLOW) {
                    continue;
                }

                // Context for directory entry event is the file name of entry
                @SuppressWarnings("unchecked")
                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path child = dir.resolve(ev.context());

                if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                    continue;
                }

                this.processor.onEvent(child, ev.kind());

            }

            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);

                // all directories are inaccessible
                if (keys.isEmpty()) {
                    break;
                }
            }
        }
    }

    public void close() throws IOException {
        this.running = false;
        this.watcher.close();
    }
}
