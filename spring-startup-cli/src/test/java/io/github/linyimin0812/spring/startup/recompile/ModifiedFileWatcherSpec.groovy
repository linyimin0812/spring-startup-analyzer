package io.github.linyimin0812.spring.startup.recompile

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

import java.lang.reflect.Field

/**
 * @author linyimin
 * */
@Stepwise
class ModifiedFileWatcherSpec extends Specification {
    @Shared
    ModifiedFileProcessor processor = new ModifiedFileProcessor()

    @Shared
    ModifiedFileWatcher watcher = new ModifiedFileWatcher(processor)

    def "test watcher"() {
        when:
        Field runningField = watcher.getClass().getDeclaredField("running")
        runningField.setAccessible(true)

        then:
        runningField.getBoolean(watcher)
    }

    def "test close"() {
        when:
        Field runningField = watcher.getClass().getDeclaredField("running")
        runningField.setAccessible(true)
        watcher.close()

        then:
        !runningField.getBoolean(watcher)
        
    }
}
