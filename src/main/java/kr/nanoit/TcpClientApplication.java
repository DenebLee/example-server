package kr.nanoit;

import lombok.extern.slf4j.Slf4j;

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

    public static final int TOTAL_COUNT = 100;
    private static final AtomicInteger readCounter = new AtomicInteger(0);
    private static final AtomicInteger writeCounter = new AtomicInteger(0);

    public static void main(String[] args) {

        try {


            String authData = "{\"type\": \"AUTHENTICATION\",\"messageUuid\": \"1\",\"data\": {\"agent_id\":\"1\",\"username\":\"이정섭\", \"password\": \"이정섭\", \"email\": \"test@test.com\"}}" + "\r\n";
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
                        Thread.sleep(600L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }
                System.out.println("Monitor Thread 정상 종료");
            });
            monitor.setDaemon(true);
            monitor.start();

            Thread writeThread = new Thread(() -> {
                for (int i = 0; i < TOTAL_COUNT; i++) {
                    try {
                        if (i == 0) {
                            dataOutputStream.write(payload);
                            dataOutputStream.flush();
                            writeCounter.incrementAndGet();
                        }
                        if (i > 0) {
                            String data = "{\"type\": \"SEND\",\"messageUuid\": \"" + i + "\"  ,\"data\": " +
                                    "{\"agent_id\": 1, \"sender_num\": \"010-4444-5555\", \"sender_callback\": \"053-555-4444\", \"sender_name\": \"이정섭\"," +
                                    "\"content\": \" 테스트중 \"}}" + "\r\n";
                            byte[] payload1 = data.getBytes(StandardCharsets.UTF_8);

                            dataOutputStream.write(payload1);
                            dataOutputStream.flush();
                            writeCounter.incrementAndGet();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//
                }
                countDownLatch.countDown();
                System.out.println("Write Thread 정상 종료");
            });
            writeThread.start();

            Thread readThread = new Thread(() -> {
                for (int i = 0; i < TOTAL_COUNT; i++) {
                    try {
                        String readPayload = bufferedReader.readLine();
                        System.out.println(readPayload);
                        readCounter.incrementAndGet();
                        if (readPayload == null) {
                            socket.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    countDownLatch.countDown();
                }
                System.out.println("ReadThread 정상 종료");
            });
            readThread.start();

            countDownLatch.await();

            System.out.println("정상 종료");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
