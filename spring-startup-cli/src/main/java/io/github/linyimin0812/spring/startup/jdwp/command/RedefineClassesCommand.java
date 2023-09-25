package io.github.linyimin0812.spring.startup.jdwp.command;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author linyimin
 **/
public class RedefineClassesCommand extends CommandPackage<RedefineClassesCommand.Data> {

    public RedefineClassesCommand(Data data) {
        super(11 + data.length(), CommandPackage.nextId(), (byte) 0, (byte) 1, (byte) 18, data);
    }

    @Override
    byte[] dataBytes() {
        return getData().toBytes();
    }

    public static class Data {
        // Number of reference types that follow
        private final int classes;

        private final List<RedefineClass> redefineClasses;

        public Data(List<RedefineClass> redefineClasses) {
            this.classes = redefineClasses.size();
            this.redefineClasses = redefineClasses;
        }

        public int length() {
            return 4 + redefineClasses.stream().mapToInt(RedefineClass::length).sum();
        }

        public byte[] toBytes() {

            ByteBuffer buffer = ByteBuffer.allocate(length());

            buffer.putInt(classes);

            for (RedefineClass redefineClass : redefineClasses) {
                buffer.put(redefineClass.toBytes());
            }

            return buffer.array();
        }

        public int getClasses() {
            return classes;
        }
    }

    public static class RedefineClass {
        // The reference type
        private final long referenceTypeId;

        // Number of bytes defining class (below)
        private final int numOfBytes;

        // byte in JVM class file format
        private final byte[] classBytes;


        public RedefineClass(long referenceTypeId, byte[] classBytes) {
            this.referenceTypeId = referenceTypeId;
            this.numOfBytes = classBytes.length;
            this.classBytes = classBytes;
        }

        public int length() {
            return 8 + 4 + numOfBytes;
        }

        public byte[] toBytes() {

            ByteBuffer buffer = ByteBuffer.allocate(length());

            buffer.putLong(referenceTypeId);
            buffer.putInt(numOfBytes);
            buffer.put(classBytes);

            return buffer.array();
        }
    }
}
