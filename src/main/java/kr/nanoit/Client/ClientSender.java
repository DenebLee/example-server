package kr.nanoit.Client;

import com.fasterxml.jackson.core.JsonProcessingException;
import kr.nanoit.Client.payload.ClientAuthentication;
import kr.nanoit.Client.payload.ClientPayload;
import kr.nanoit.Client.payload.ClientPayloadType;
import kr.nanoit.Client.payload.ClientSend;
import kr.nanoit.domain.payload.Payload;
import kr.nanoit.extension.Jackson;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientSender implements Runnable {

    private int count;
    private Socket socket;
    private CountDownLatch countDownLatch;
    private AtomicBoolean isAuthenticated;

    public ClientSender(int count, Socket socket, CountDownLatch countDownLatch, AtomicBoolean isAuthenticated) {
        this.count = count;
        this.socket = socket;
        this.countDownLatch = countDownLatch;
        this.isAuthenticated = isAuthenticated;
    }

    @Override
    public void run() {
        try {
            String authenticationPacket = makeAuthentication();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // auth
            bufferedWriter.write(authenticationPacket);
            bufferedWriter.flush();
            TcpClientApplication.writeCounter.incrementAndGet();

            while (!isAuthenticated.get()) {
                System.out.println("sender: wait authentication");
                Thread.sleep(1000L);
            }
            System.out.println("sender: authenticated!");

            // send data
            for (int i = 1; i <= count; i++) {
                String sendData = makeSendPacket(i);
                bufferedWriter.write(sendData);
                bufferedWriter.flush();
                TcpClientApplication.writeCounter.incrementAndGet();

                if (TcpClientApplication.writeCounter.get() == count) {
                    System.out.println("전송 완료 ");
                }
            }
            countDownLatch.countDown();
            System.out.println("Write Thread 정상 종료");
        } catch (IOException | InterruptedException e) { // 한번 실패나면 전체 작업 종료
            e.printStackTrace();
        }
    }

    private String makeSendPacket(int messageId) throws JsonProcessingException {
        return Jackson.getInstance().getObjectMapper().writeValueAsString(new ClientPayload()
                .setType(ClientPayloadType.SEND)
                .setMessageUuid(messageId)
                .setData(new ClientSend()
                        .setAgent_id(1)
                        .setSender_num("010-4444-5555")
                        .setSender_callback("053-793-9405")
                        .setSender_name("이정섭")
                        .setContent(String.valueOf(messageId)))) + "\n";
    }

    private String makeAuthentication() throws JsonProcessingException {
        return Jackson.getInstance().getObjectMapper().writeValueAsString(new ClientPayload()
                .setType(ClientPayloadType.AUTHENTICATION)
                .setMessageUuid(0)
                .setData(new ClientAuthentication()
                        .setAgent_id(1)
                        .setUsername("이정섭")
                        .setPassword("이정섭")
                        .setEmail("test@test.com"))) + "\n";
    }
}