package kr.nanoit;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class TestClient {
    public Socket socket;
    private DataOutputStream dataOutputStream;
    private BufferedReader bufferedReader;


    public void connect(int port) {
        try {
            Socket socketValue = new Socket();
            socketValue.connect(new InetSocketAddress("localhost", port));
            this.socket = socketValue;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() throws IOException {
        socket.close();
    }

    public void delay() throws InterruptedException {
        Thread.sleep(5000);
    }


    public void writeWithAuthenticaion(int count, String value) throws IOException, InterruptedException {
        String authData = "{\"type\": \"AUTHENTICATION\",\"messageUuid\": \"1\",\"data\": {\"agent_id\":\"1\",\"username\":\"이정섭\", \"password\": \"이정섭\", \"email\": \"test@test.com\"}}" + "\r\n";
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
        byte[] authPayload = authData.getBytes(StandardCharsets.UTF_8);
        byte[] messagePayload = value.getBytes(StandardCharsets.UTF_8);
        for (int i = 0; i < count; i++) {
            if (i == 0) {
                dataOutputStream.write(authPayload);
                Thread.sleep(1000);
            }
            dataOutputStream.write(messagePayload);
        }
    }

    public void write(String value, int count) throws IOException, InterruptedException {
        value = value + "\r\n";
        dataOutputStream = new DataOutputStream(socket.getOutputStream());
        for (int i = 0; i < count; i++) {
            byte[] payload = value.getBytes(StandardCharsets.UTF_8);
            dataOutputStream.write(payload);
            Thread.sleep(500);
        }
    }

    public String read(int count) throws IOException {
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        for (int i = 0; i < count; i++) {
            String readPayload = bufferedReader.readLine();
            System.out.println("서버에서 보낸 답장" + readPayload);
            if (readPayload != null) {
                return readPayload;
            }
        }
        return null;
    }
}
