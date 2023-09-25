package io.github.linyimin0812.spring.startup.jdwp.command;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author linyimin
 **/
public class AllClassesReplyPackage extends ReplyPackage<List<AllClassesReplyPackage.Data>> {

    public AllClassesReplyPackage(ByteBuffer buffer) {
        super(buffer);
    }

    @Override
    List<Data> parseData(ByteBuffer buffer) {
        // Number of reference types that follow
        int classes = buffer.getInt();

        List<Data> list = new ArrayList<>(classes);

        for (int i = 0; i < classes; i++) {
            byte refTypeTag = buffer.get();
            long referenceTypeId = buffer.getLong();

            int stringLength = buffer.getInt();
            byte[] signatureBytes = new byte[stringLength];
            buffer.get(signatureBytes, 0, stringLength);
            String signature = new String(signatureBytes, StandardCharsets.UTF_8);

            int status = buffer.getInt();

            list.add(new Data(refTypeTag, referenceTypeId, signature, status));
        }

        return list;
    }


    public static class Data {

        private final byte refTypeTag;
        private final long referenceTypeId;
        private final String signature;
        private final int status;

        public Data(byte refTypeTag, long referenceTypeId, String signature, int status) {
            this.refTypeTag = refTypeTag;
            this.referenceTypeId = referenceTypeId;
            this.signature = signature;
            this.status = status;
        }

        public byte getRefTypeTag() {
            return refTypeTag;
        }

        public long getReferenceTypeId() {
            return referenceTypeId;
        }

        public String getSignature() {
            return signature;
        }

        public int getStatus() {
            return status;
        }
    }

}
