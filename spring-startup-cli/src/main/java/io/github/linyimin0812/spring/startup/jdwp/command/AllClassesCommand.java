package io.github.linyimin0812.spring.startup.jdwp.command;

/**
 * @author linyimin
 **/
public class AllClassesCommand<Void> extends CommandPackage<Void> {


    public AllClassesCommand() {
        super(CommandPackage.nextId(), (byte) 0, (byte) 1, (byte) 3);
    }

    @Override
    public byte[] dataBytes() {
        return new byte[0];
    }
}
