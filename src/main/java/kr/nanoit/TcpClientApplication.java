package kr.nanoit;

import com.fasterxml.jackson.core.JsonProcessingException;
import kr.nanoit.extension.Jackson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class TcpClientApplication {

    private static final AtomicInteger readCounter = new AtomicInteger(0);
    private static final AtomicInteger writeCounter = new AtomicInteger(0);

    private static final AtomicInteger reportSuccessCounter = new AtomicInteger(0);
    private static final AtomicInteger reportFailCounter = new AtomicInteger(0);

    private static final AtomicInteger sendAckSuccessCounter = new AtomicInteger(0);
    private static final AtomicInteger sendAckFailCounter = new AtomicInteger(0);

    public static void main(String[] args) throws JsonProcessingException {
        System.out.println(CommonPacket.makeSendPacket(1));
        Scanner scanner = new Scanner(System.in);
        System.out.println("전송할 메시지 갯수 입력 ");
        int totalSendCount = scanner.nextInt();

        AtomicBoolean isAuthenticated = new AtomicBoolean(false);

        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress("192.168.0.64", 12323));

            while (!socket.isConnected()) {
                Thread.sleep(1000L);
            }

            CountDownLatch simulatorWait = new CountDownLatch(2);

            Thread monitor = new Thread(TcpClientApplication::monitor);
            monitor.setDaemon(true);
            monitor.start();

            new Thread(() -> sender(totalSendCount, socket, simulatorWait, isAuthenticated)).start();
            new Thread(() -> reader(totalSendCount, socket, simulatorWait, isAuthenticated)).start();

            simulatorWait.await();

            System.out.println("정상 종료");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void reader(int totalSendCount, Socket socket, CountDownLatch countDownLatch, AtomicBoolean isAuthenticated) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true) {
                String readPayload = bufferedReader.readLine();

                if (isAuthenticated.get() == false) {
                    TestAuthAck authenticationAck = Jackson.getInstance().getObjectMapper().readValue(readPayload, TestAuthAck.class);
                    System.out.println("reader: auth: [" + authenticationAck + "]");

                    if (authenticationAck.getData().getResult().equals("FAILED")) {
                        throw new Exception("authentication failed");
                    } else {
                        isAuthenticated.set(true);
                    }
                } else {
                    System.out.println("reader: data: [" + readPayload + "]");
                    readCounter.incrementAndGet();
                    if (readPayload == null) {
                        socket.close();
                    }


                    //
                    TestCommonReceivePacket receivedPacket = Jackson.getInstance().getObjectMapper().readValue(readPayload, TestCommonReceivePacket.class);
                    if (receivedPacket.getType().equals("SEND_ACK")) {
                        Map<String, String> data = (Map) receivedPacket.getData();
                        String result = data.get("result");
                        if (result != null && result.equals("SUCCESS")) {
                            sendAckSuccessCounter.incrementAndGet();
                        } else {
                            sendAckFailCounter.incrementAndGet();
                        }
                    } else if (receivedPacket.getType().equals("REPORT")) {
                        Map<String, String> data = (Map) receivedPacket.getData();
                        String result = data.get("result");
                        if (result != null && result.equals("SUCCESS")) {
                            reportSuccessCounter.incrementAndGet();
                        } else {
                            reportFailCounter.incrementAndGet();
                        }
                    }

                    if (reportFailCounter.get() + reportFailCounter.get() == totalSendCount) {
                        System.out.println("READER SUCCESS TERMINATED");
                        break;
                    }
                }
            }
        } catch (Exception e) { // 한번 실패나면 전체 작업 종료
            e.printStackTrace();
        }

        countDownLatch.countDown();
        System.out.println("ReadThread Terminated");
    }

    private static void monitor() {
        while (true) {
            System.out.println("WRITE: " + writeCounter.get() + " READ: " + readCounter.get() + " SA_SUCC: " + sendAckSuccessCounter.get() + " SA_FAIL: " + sendAckFailCounter.get() + " RE_SUCC: " + reportSuccessCounter.get() + " RE_FAIL: " + reportFailCounter.get());
            try {
                Thread.sleep(600L);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
        System.out.println("Monitor Thread 정상 종료");
    }

    private static void sender(int count, Socket socket, CountDownLatch countDownLatch, AtomicBoolean isAuthenticated) {
        try {
            String authenticationPacket = CommonPacket.makeAuthentication();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // auth
            bufferedWriter.write(authenticationPacket + "\n");
            bufferedWriter.flush();
            writeCounter.incrementAndGet();

            while (!isAuthenticated.get()) {
                System.out.println("sender: wait authentication");
                Thread.sleep(1000L);
            }
            System.out.println("sender: authenticated!");

            // send data
            for (int i = 1; i <= count; i++) {
                String data = CommonPacket.makeSendPacket(i);
                bufferedWriter.write(data + "\n");
                bufferedWriter.flush();
                writeCounter.incrementAndGet();

                if (writeCounter.get() == count) {
                    System.out.println("전송 완료 ");
                }
            }
            countDownLatch.countDown();
            System.out.println("Write Thread 정상 종료");
        } catch (IOException | InterruptedException e) { // 한번 실패나면 전체 작업 종료
            e.printStackTrace();
        }
    }
}

@Getter
@Setter
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
class TestAuthAck {
    private String type;
    private String messageUuid;
    private Data data;

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Data {

        private long agent_id;
        private String result;
    }
}

@Getter
@Setter
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
class TestCommonReceivePacket {
    private String type;
    private String messageUuid;
    private Object data;
}

@Getter
@Setter
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
class TestSendAck {
    private String type;
    private String messageUuid;
    private Data data;

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class Data {
        private String result;
    }
}

@Getter
@Setter
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
class CommonPacket {
    private String type;
    private String messageUuid;
    private Object data;

    @Getter
    @Setter
    @Accessors(chain = true)
    @AllArgsConstructor
    @NoArgsConstructor
    static class Authentication {
        private String agent_id;
        private String username;
        private String password;
        private String email;
    }

    @Getter
    @Setter
    @Accessors(chain = true)
    @AllArgsConstructor
    @NoArgsConstructor
    static class Send {
        private String agent_id;
        private String sender_num;
        private String sender_callback;
        private String sender_name;
        private String content;
    }

    public static String makeSendPacket(int messageId) throws JsonProcessingException {
        return Jackson.getInstance().getObjectMapper().writeValueAsString(new CommonPacket()
                .setType("SEND")
                .setMessageUuid(String.valueOf(messageId))
                .setData(new CommonPacket.Send()
                        .setAgent_id("1")
                        .setSender_num("010-4444-5555")
                        .setSender_callback("053-793-9405")
                        .setSender_name("이정섭")
                        .setContent(String.valueOf(messageId))));
    }

    public static String makeAuthentication() throws JsonProcessingException {
        return Jackson.getInstance().getObjectMapper().writeValueAsString(new CommonPacket()
                .setType("AUTHENTICATION")
                .setMessageUuid("1")
                .setData(new CommonPacket.Authentication()
                        .setAgent_id("1")
                        .setUsername("이정섭")
                        .setPassword("이정섭")
                        .setEmail("test@test.com")));
    }
}
