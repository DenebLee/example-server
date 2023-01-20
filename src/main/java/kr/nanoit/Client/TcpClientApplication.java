package kr.nanoit.Client;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class TcpClientApplication {

    protected static final AtomicInteger readCounter = new AtomicInteger(0);
    protected static final AtomicInteger writeCounter = new AtomicInteger(0);

    protected static final AtomicInteger reportSuccessCounter = new AtomicInteger(0);
    protected static final AtomicInteger reportFailCounter = new AtomicInteger(0);

    protected static final AtomicInteger sendAckSuccessCounter = new AtomicInteger(0);
    protected static final AtomicInteger sendAckFailCounter = new AtomicInteger(0);

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.println("전송할 메시지 갯수 입력 ");


        int totalSendCount = scanner.nextInt();

        System.out.println("전송할 Ip 입력");
        String ip = scanner.next();

        AtomicBoolean isAuthenticated = new AtomicBoolean(false);


        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(ip, 12323));

            while (!socket.isConnected()) {
                Thread.sleep(1000L);
            }

            CountDownLatch simulatorWait = new CountDownLatch(2);

            Thread monitor = new Thread(new ClientMonitor());
            Thread sender = new Thread(new ClientSender(totalSendCount, socket, simulatorWait, isAuthenticated));
            Thread reader = new Thread(new ClientReader(totalSendCount, socket, simulatorWait, isAuthenticated));


            monitor.setDaemon(true);

            monitor.start();
            sender.start();
            reader.start();


            simulatorWait.await();

            System.out.println("정상 종료");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

