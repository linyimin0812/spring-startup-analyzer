package io.github.linyimin0812.spring.startup.jdwp;

import com.alibaba.fastjson.JSON;
import io.github.linyimin0812.spring.startup.jdwp.command.AllClassesCommand;
import io.github.linyimin0812.spring.startup.jdwp.command.AllClassesReplyPackage;
import io.github.linyimin0812.spring.startup.jdwp.command.RedefineClassesCommand;
import io.github.linyimin0812.spring.startup.jdwp.command.RedefineClassesReplyPackage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class JDWPClient {

    private final Socket socket;
    private final String host;
    private final Integer port;

    public final static int LENGTH_SIZE = 4;

    public JDWPClient(String host, int port) throws IOException {
        this.host = host;
        this.port = port;
        this.socket = new Socket(host, port);
    }

    public boolean start() throws IOException {

        // 发送 JDWP 握手请求
        String handshakeCommand = "JDWP-Handshake";

        InputStream in = socket.getInputStream();
        OutputStream out = socket.getOutputStream();

        out.write(handshakeCommand.getBytes(StandardCharsets.UTF_8));
        byte[] handshakeResponseBytes = new byte[14];
        int bytesRead = in.read(handshakeResponseBytes);

        boolean isConnected = bytesRead == handshakeResponseBytes.length && handshakeCommand.equals(new String(handshakeResponseBytes, StandardCharsets.UTF_8));

        if (isConnected) {
            System.out.printf("[INFO] Connected to the target VM, address: '%s:%s', transport: 'socket'\n", host, port);
        }

        return isConnected;
    }

    public void close() throws IOException {
        if (!this.socket.isClosed()) {
            this.socket.close();
        }
    }

    public synchronized ByteBuffer execute(byte[] command) throws IOException {

        OutputStream out = this.socket.getOutputStream();
        InputStream in = this.socket.getInputStream();

        out.write(command);

        byte[] replyBytes = new byte[LENGTH_SIZE];
        int bytesRead = 0;

        while (bytesRead!= LENGTH_SIZE) {
            bytesRead += in.read(replyBytes, bytesRead, LENGTH_SIZE - bytesRead);
        }

        int length = ByteBuffer.wrap(replyBytes).getInt();

        ByteBuffer buffer = ByteBuffer.allocate(length);

        buffer.putInt(length);

        while (bytesRead != length) {
            replyBytes = new byte[4096];
            int currentBytesRead = in.read(replyBytes);
            buffer.put(replyBytes, 0, currentBytesRead);
            bytesRead += currentBytesRead;
        }

        buffer.flip();

        return buffer;
    }


    public static void main(String[] args) throws IOException, InterruptedException {

        JDWPClient client = new JDWPClient("127.0.0.1", 5005);

        if (!client.start()) {
            System.out.println("start error.");
            return;
        }

        AllClassesCommand<Void> allClassesCommand = new AllClassesCommand<>();

        ByteBuffer buffer = client.execute(allClassesCommand.toBytes());

        AllClassesReplyPackage replyPackage = new AllClassesReplyPackage(buffer);

        System.out.println(JSON.toJSONString(replyPackage, true));

        AllClassesReplyPackage.Data data = replyPackage.getData().stream().filter(data1 -> data1.getSignature().contains("MainController$Test;")).findFirst().get();

        byte[] bytes = Files.readAllBytes(Paths.get("/Users/banzhe/IdeaProjects/project/spring-boot-async-bean-demo/target/classes/io/github/linyimin0812/controller/MainController$Test.class"));

        RedefineClassesCommand.RedefineClass redefineClass = new RedefineClassesCommand.RedefineClass(data.getReferenceTypeId(), bytes);


        List<RedefineClassesCommand.RedefineClass> list = Collections.singletonList(redefineClass);

        RedefineClassesCommand redefineClassesCommand = new RedefineClassesCommand(new RedefineClassesCommand.Data(list));

        buffer = client.execute(redefineClassesCommand.toBytes());

        RedefineClassesReplyPackage redefineClassesReplyPackage = new RedefineClassesReplyPackage(buffer);


    }
}
