package kr.nanoit;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class TcpClientApplication {

    public static final int TOTAL_COUNT = 5;
    private static final AtomicInteger readCounter = new AtomicInteger(0);
    private static final AtomicInteger writeCounter = new AtomicInteger(0);

    public static void main(String[] args) throws IOException, InterruptedException {
        String data = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "\r\n";
        byte[] payload = data.getBytes(StandardCharsets.UTF_8);
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress("localhost", 12323));
        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());

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
                } catch (IOException e) {
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
                    dataInputStream.read(payload);
                    System.out.println(new String(payload));
                    readCounter.incrementAndGet();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            countDownLatch.countDown();
        });
        readThread.setDaemon(true);
        readThread.start();

        countDownLatch.await();
    }
}
