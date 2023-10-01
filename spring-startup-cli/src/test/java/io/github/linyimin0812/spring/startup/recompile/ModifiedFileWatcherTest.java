package io.github.linyimin0812.spring.startup.recompile;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;

/**
 * @author linyimin
 **/
class ModifiedFileWatcherTest {

    @Test
    void close() throws IOException, NoSuchFieldException, IllegalAccessException {
        ModifiedFileProcessor processor = new ModifiedFileProcessor();
        ModifiedFileWatcher watcher = new ModifiedFileWatcher(processor);

        Field runningField = watcher.getClass().getDeclaredField("running");
        runningField.setAccessible(true);

        Assertions.assertTrue(runningField.getBoolean(watcher));

        watcher.close();

        Assertions.assertFalse(runningField.getBoolean(watcher));

    }

}