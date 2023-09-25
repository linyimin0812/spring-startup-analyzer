package io.github.linyimin0812.spring.startup.jdwp.command;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author linyimin
 **/
public class CommandCache {

    private static final ConcurrentMap<Integer, CommandPackage<?>> COMMAND_CACHE = new ConcurrentHashMap<>();

    public static synchronized void cache(Integer id, CommandPackage<?> command) {
        COMMAND_CACHE.put(id, command);
    }

    public static synchronized CommandPackage<?> poll(Integer id) {
        return COMMAND_CACHE.remove(id);
    }

}
