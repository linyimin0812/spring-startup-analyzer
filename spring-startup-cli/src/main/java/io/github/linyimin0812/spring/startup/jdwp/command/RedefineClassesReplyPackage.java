package io.github.linyimin0812.spring.startup.jdwp.command;

import java.nio.ByteBuffer;

/**
 * @author linyimin
 **/
public class RedefineClassesReplyPackage extends ReplyPackage<Void> {
    public RedefineClassesReplyPackage(ByteBuffer buffer) {
        super(buffer);
    }

    @Override
    Void parseData(ByteBuffer buffer) {
        return null;
    }

}
