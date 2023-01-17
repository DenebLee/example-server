package kr.nanoit;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class TestClient implements Runnable {
    public Socket socket;
    private DataOutputStream dataOutputStream;
    private BufferedReader bufferedReader;
    private ArrayList<String> dataList;


    public TestClient(int port) throws IOException {
        this.dataList = new ArrayList<>();
        this.socket = new Socket();
        this.socket.connect(new InetSocketAddress("localhost", port));

        this.dataOutputStream = new DataOutputStream(socket.getOutputStream());
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void run() {
        try {
            while (true) {
                String value = bufferedReader.readLine();
                if (value != null) {
                    dataList.add(value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void write(String value, int count) throws IOException, InterruptedException {
        value = value + "\r\n";
        for (int i = 0; i < count; i++) {
            byte[] payload = value.getBytes(StandardCharsets.UTF_8);
            dataOutputStream.write(payload);
            Thread.sleep(500);
        }
    }

    public void writeWithAuth(int count) throws IOException, InterruptedException {
        String authData = "{\"type\": \"AUTHENTICATION\",\"messageUuid\": \"1\",\"data\": {\"agent_id\":\"1\",\"username\":\"이정섭\", \"password\": \"이정섭\", \"email\": \"test@test.com\"}}" + "\r\n";

        byte[] authPayload = authData.getBytes(StandardCharsets.UTF_8);

        dataOutputStream.write(authPayload);
        dataOutputStream.flush();
        System.out.println("인증 Data 전송 완료");
        Thread.sleep(1000);
        for (int i = 0; i < count; i++) {
            String sendData = "{\"type\": \"SEND\",\"messageUuid\": \"1\",\"data\": " +
                    "{\"agent_id\": 1, \"sender_num\": \"010-4444-5555\", \"sender_callback\": \"053-555-4444\", \"sender_name\": \"이정섭\"," +
                    "\"content\": \" 테스트중 '" + i + "'\"  }}" + "\n";
            byte[] sendPayload = sendData.getBytes(StandardCharsets.UTF_8);

            dataOutputStream.write(sendPayload);
            System.out.println("send Data 전송 완료");
        }
    }

    public String getResponseData() {
        System.out.println(dataList.get(0));
        return dataList.get(0);
    }

    public void delay() throws InterruptedException {
        Thread.sleep(6000);
    }

    public boolean isSocketClose() {
        return socket.isClosed();
    }
}