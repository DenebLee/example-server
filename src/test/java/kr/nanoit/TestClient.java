package kr.nanoit;

import kr.nanoit.module.inbound.socket.SocketResource;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TestClient {
    private final Socket socket;
    private DataOutputStream dataOutputStream;

    public TestClient() {
        this.socket = new Socket();
    }

    public void connect() {
        try {
            socket.connect(new InetSocketAddress("localhost", 24242));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() throws IOException {
        socket.close();
    }

    public void write(String value) throws IOException {
        value = value + "\r\n";
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
        byte[] payload = value.getBytes(StandardCharsets.UTF_8);
        dataOutputStream.write(payload);
    }

}
