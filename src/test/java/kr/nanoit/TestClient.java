package kr.nanoit;

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

    public void connect(int port) {
        try {
            socket.connect(new InetSocketAddress("localhost", port));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() throws IOException {
        socket.close();
    }

    public void delay() throws InterruptedException {
        Thread.sleep(3000);
    }


    public void write(String value) throws IOException {

        value = value + "\r\n";
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
        byte[] payload = value.getBytes(StandardCharsets.UTF_8);
        dataOutputStream.write(payload);
    }

}
