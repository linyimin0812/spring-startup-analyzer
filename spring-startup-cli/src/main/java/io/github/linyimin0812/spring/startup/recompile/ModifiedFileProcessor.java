package io.github.linyimin0812.spring.startup.recompile;

import io.github.linyimin0812.spring.startup.cli.CliMain;
import io.github.linyimin0812.spring.startup.constant.Constants;
import io.github.linyimin0812.spring.startup.jdwp.JDWPClient;
import io.github.linyimin0812.spring.startup.jdwp.command.AllClassesCommand;
import io.github.linyimin0812.spring.startup.jdwp.command.AllClassesReplyPackage;
import io.github.linyimin0812.spring.startup.jdwp.command.RedefineClassesCommand;
import io.github.linyimin0812.spring.startup.jdwp.command.RedefineClassesReplyPackage;
import io.github.linyimin0812.spring.startup.utils.ModuleUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.github.linyimin0812.spring.startup.constant.Constants.OUT;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

/**
 * @author linyimin
 **/
public class ModifiedFileProcessor {

    private final Map<String, WatchEvent.Kind<Path>> FILE_WATCH_EVENTS = new ConcurrentHashMap<>();
    private final Map<String /* class qualifier */, Path> RECOMIPLED_FILE_MAP = new ConcurrentHashMap<>();

    public ModifiedFileProcessor() {
        // TODO: 初始化时从git获取所有修改文件
    }

    public void onEvent(Path dir, WatchEvent.Kind<Path> eventKind) {

        String path = dir.toString();

        if (!FILE_WATCH_EVENTS.containsKey(path)) {
            OUT.printf("\n[INFO] - [%s] %s\n", eventKind.name().replace("ENTRY_", Constants.EMPTY_STRING), path);
            OUT.printf(CliMain.prompt());
        }

        FILE_WATCH_EVENTS.put(path, eventKind);
    }

    /**
     * @param client jdwp client
     * @throws IOException io exception
     */
    public void process(JDWPClient client) throws IOException {

        if (!check()) {
            return;
        }

        Map<String, Long> loadedClass = acquireLoadedClass(client);

        RedefineClassesCommand redefineClassesCommand = buildRedefineClassesCommand(loadedClass);

        RedefineClassesReplyPackage replyPackage = new RedefineClassesReplyPackage(client.execute(redefineClassesCommand.toBytes()));

        printHostSwapInfo(redefineClassesCommand.getData().getClasses(), replyPackage);

        FILE_WATCH_EVENTS.clear();
        RECOMIPLED_FILE_MAP.clear();

    }

    public boolean check() {

        if (FILE_WATCH_EVENTS.isEmpty()) {
            OUT.println("There are no file changes, don't need to hotswap.");
            return false;
        }

        boolean anyAdded = FILE_WATCH_EVENTS.values().stream().anyMatch(kind -> kind == ENTRY_CREATE);

        if (anyAdded) {
            OUT.println("Hotswap does not support adding new files, please restart the application");
            return false;
        }

        return true;
    }

    private void printHostSwapInfo(int classes, RedefineClassesReplyPackage replyPackage) {

        if (!replyPackage.isSuccess()) {
            OUT.printf("Hotswap failed, error code: %s\n", replyPackage.getErrorCode());
            return;
        }

        OUT.printf("[INFO] hotswap success, reloaded classes: %s\n", classes);

        for (Map.Entry<String, Path> entry : RECOMIPLED_FILE_MAP.entrySet()) {
            OUT.printf("  class - %s\n", entry.getKey());
            OUT.printf("  |_ file - %s\n", entry.getValue());
        }
    }

    private Map<String /* qualifier */, Long> acquireLoadedClass(JDWPClient client) throws IOException {

        AllClassesCommand<Void> allClassesCommand = new AllClassesCommand<>();
        AllClassesReplyPackage allClassesReplyPackage = new AllClassesReplyPackage(client.execute(allClassesCommand.toBytes()));

        Map<String, Long> cache = new HashMap<>();

        for (AllClassesReplyPackage.Data data : allClassesReplyPackage.getData()) {
            if (data.getRefTypeTag() != 1 && data.getRefTypeTag() != 2) {
                continue;
            }
            // format of signature: Lio/github/linyimin0812/controller/MainController$Test;
            String classQualifier = data.getSignature().substring(1, data.getSignature().length() - 1).replace(File.separator, Constants.DOT);
            cache.put(classQualifier, data.getReferenceTypeId());
        }

        return cache;
    }

    private RedefineClassesCommand buildRedefineClassesCommand(Map<String, Long> loadedClass) throws IOException {

        RECOMIPLED_FILE_MAP.putAll(acquireRecompiledFileMap());

        List<RedefineClassesCommand.RedefineClass> redefineClasses = new ArrayList<>();

        if (RECOMIPLED_FILE_MAP.isEmpty()) {
            return new RedefineClassesCommand(new RedefineClassesCommand.Data(redefineClasses));
        }

        for (Map.Entry<String, Path> entry : RECOMIPLED_FILE_MAP.entrySet()) {

            long referenceTypeId = loadedClass.getOrDefault(entry.getKey(), 0L);

            if (referenceTypeId == 0L) {
                OUT.println("[WARN] class not found: " + entry.getKey() + ", and will not be hotswap");
                continue;
            }

            byte[] classData = Files.readAllBytes(entry.getValue());

            RedefineClassesCommand.RedefineClass redefineClass = new RedefineClassesCommand.RedefineClass(referenceTypeId, classData);

            redefineClasses.add(redefineClass);

        }

        return new RedefineClassesCommand(new RedefineClassesCommand.Data(redefineClasses));
    }

    private Map<String /* class qualifier */, Path> acquireRecompiledFileMap() throws IOException {

        Map<String, Path> recompileFileMap = new HashMap<>();

        for (String key : FILE_WATCH_EVENTS.keySet()) {

            Path changeFile = Paths.get(key);

            String fileDir = changeFile.getParent().toString();
            String filePackage = getPackage(fileDir);

            Path home = Paths.get(System.getProperty(Constants.USER_DIR));

            String compilePath;

            if (ModuleUtil.isMaven(home)) {
                compilePath = fileDir.replace(Constants.SOURCE_DIR, Constants.MAVEN_COMPILE_DIR);
            } else if (ModuleUtil.isGradle(home)) {
                compilePath = fileDir.replace(Constants.SOURCE_DIR, Constants.GRADLE_COMPILE_DIR);
            } else {
                OUT.printf("[WARN] file %s is not managed by maven or gradle, skip directly.\n", key);
                continue;
            }

            String fileNameWithoutPrefix = changeFile.getFileName().toString().replace(Constants.SOURCE_PREFIX, Constants.EMPTY_STRING);

            Files.walkFileTree(Paths.get(compilePath), new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {

                    String name = file.getFileName().toString();
                    if (name.equals(fileNameWithoutPrefix + Constants.COMPILE_PREFIX) || name.contains(fileNameWithoutPrefix + Constants.DOLLAR)) {
                        String qualifier = filePackage + name.replace(Constants.COMPILE_PREFIX, Constants.EMPTY_STRING);
                        recompileFileMap.put(qualifier, file);
                    }

                    return FileVisitResult.CONTINUE;
                }
            });

        }

        return recompileFileMap;
    }

    /**
     *
     * @param fileDir absolute file path string
     * @return package, for example: io.github.linyimin0812.spring.startup.jdwp.
     */
    private String getPackage(String fileDir) {

        int startIndex = fileDir.indexOf(Constants.SOURCE_DIR) + Constants.SOURCE_DIR.length() + 1;

        if (startIndex >= fileDir.length()) {
            return Constants.EMPTY_STRING;
        }

        String filePackage = fileDir.substring(startIndex).replace(File.separator, Constants.DOT);

        return filePackage.endsWith(Constants.DOT) ? filePackage : filePackage + Constants.DOT;
    }
}
