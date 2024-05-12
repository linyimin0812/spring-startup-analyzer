package io.github.linyimin0812.spring.startup.jdwp;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static io.github.linyimin0812.spring.startup.constant.Constants.OUT;

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
            OUT.printf("[INFO] Connected to the target VM, address: '%s:%s', transport: 'socket'\n", host, port);
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
}
