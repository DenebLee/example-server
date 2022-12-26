package kr.nanoit;

import com.fasterxml.jackson.databind.deser.std.StringDeserializer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class TcpClientApplication {

    public static final int TOTAL_COUNT = 6000;
    private static final AtomicInteger readCounter = new AtomicInteger(0);
    private static final AtomicInteger writeCounter = new AtomicInteger(0);

    public static void main(String[] args) {
        try {
            String data = "{\"type\": \"SEND\",\"messageUuid\": \"test01\",\"data\": " +
                    "{\"id\": 123123, \"phone\": \"01044445555\", \"callback\": \"053555444\", " +
                    "\"content\": \" 안녕하세요\"}}" + "\r\n";

            String authData = "{\"type\": \"AUTHENTICATION\",\"messageUuid\": \"1\",\"data\": {\"username\":\"이\", \"password\": \"123123\", \"email\": \"test@test.com\"}}" + "\r\n";
            byte[] payload = authData.getBytes(StandardCharsets.UTF_8);
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress("localhost", 12323));
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            CountDownLatch countDownLatch = new CountDownLatch(2);

            Thread monitor = new Thread(() -> {
                while (true) {
                    System.out.println("WRITE: " + writeCounter.get() + " READ: " + readCounter.get());
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            monitor.setDaemon(true);
            monitor.start();

            Thread writeThread = new Thread(() -> {
                for (int i = 0; i < TOTAL_COUNT; i++) {
                    try {
                        dataOutputStream.write(payload);
                        writeCounter.incrementAndGet();
                        Thread.sleep(100000);
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                countDownLatch.countDown();
            });
            writeThread.setDaemon(true);
            writeThread.start();

            Thread readThread = new Thread(() -> {
                for (int i = 0; i < TOTAL_COUNT; i++) {
                    try {
                        Thread.sleep(1000);
                        String readPayload = bufferedReader.readLine();
                        System.out.println(readPayload);
                        if (readPayload == null) {
                            socket.close();
                        } else {
                            readCounter.incrementAndGet();
                        }
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                countDownLatch.countDown();
            });
            readThread.setDaemon(true);
            readThread.start();

            countDownLatch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
