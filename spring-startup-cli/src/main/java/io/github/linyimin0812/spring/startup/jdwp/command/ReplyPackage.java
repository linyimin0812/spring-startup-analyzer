package io.github.linyimin0812.spring.startup.jdwp.command;

import java.nio.ByteBuffer;

/**
 * @author linyimin
 **/
public abstract class ReplyPackage<T> extends Package<T> {

    private final short errorCode;

    public ReplyPackage(ByteBuffer buffer) {
        super(buffer.getInt(), buffer.getInt(), buffer.get());
        this.errorCode = buffer.getShort();

        if (this.errorCode != 0) {
            CommandPackage<?> command = CommandCache.poll(this.getId());
            System.out.printf("commandSet: %s, command: %s, errorCode: %s", command.getCommandSet(), command.getCommand(), this.errorCode);
        }

        this.setData(parseData(buffer));
    }

    public boolean isSuccess() {
        return errorCode == 0;
    }

    public short getErrorCode() {
        return errorCode;
    }

    abstract T parseData(ByteBuffer buffer);

}
