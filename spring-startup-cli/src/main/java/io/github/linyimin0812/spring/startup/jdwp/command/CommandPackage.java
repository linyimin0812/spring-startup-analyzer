package io.github.linyimin0812.spring.startup.jdwp.command;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author linyimin
 **/
public abstract class CommandPackage<T> extends Package<T> {

    private static final AtomicInteger ID_GENERATOR = new AtomicInteger(0);

    private final byte commandSet;
    private final byte command;

    public CommandPackage(int id, byte flag, byte commandSet, byte command) {
        this(11, id, flag, commandSet, command, null);
    }

    public CommandPackage(int length, int id, byte flag, byte commandSet, byte command, T data) {
        super(length, id, flag, data);
        this.commandSet = commandSet;
        this.command = command;

        CommandCache.cache(id, this);

    }

    public byte[] toBytes() {

        ByteBuffer buffer = ByteBuffer.allocate(getLength());
        buffer.putInt(getLength());
        buffer.putInt(getId());
        buffer.put(getFlag());
        buffer.put(commandSet);
        buffer.put(command);
        buffer.put(dataBytes());
        return buffer.array();
    }

    abstract byte[] dataBytes();

    public static int nextId() {
        return ID_GENERATOR.incrementAndGet();
    }

    public byte getCommandSet() {
        return commandSet;
    }

    public byte getCommand() {
        return command;
    }
}
