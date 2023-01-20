package kr.nanoit.Client;

import ch.qos.logback.core.net.server.Client;
import kr.nanoit.Client.payload.*;
import kr.nanoit.domain.message.MessageResult;
import kr.nanoit.domain.payload.ErrorPayload;
import kr.nanoit.domain.payload.Payload;
import kr.nanoit.domain.payload.PayloadType;
import kr.nanoit.extension.Jackson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientReader implements Runnable {
    private int totalSendCount;
    private Socket socket;
    private CountDownLatch countDownLatch;
    private BufferedReader bufferedReader;

    public ClientReader(int totalSendCount, Socket socket, CountDownLatch countDownLatch, AtomicBoolean isAuthenticated) {
        this.totalSendCount = totalSendCount;
        this.socket = socket;
        this.countDownLatch = countDownLatch;
        this.isAuthenticated = isAuthenticated;
    }

    private AtomicBoolean isAuthenticated;


    @Override
    public void run() {
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true) {
                String receiveData = bufferedReader.readLine();

                Payload payload = Jackson.getInstance().getObjectMapper().readValue(receiveData, Payload.class);
                if (isAuthenticated.get() == false) {
                    ClientAuthenticationAck authenticationAck = Jackson.getInstance().getObjectMapper().convertValue(payload.getData(), ClientAuthenticationAck.class);
                    System.out.println("reader: auth: [" + authenticationAck + "]");

                    if (authenticationAck.getResult().equals("FAILED")) {
                        throw new Exception("authentication failed");
                    } else {
                        isAuthenticated.set(true);
                    }
                } else {
                    System.out.println("reader: data: [" + receiveData + "]");
                    TcpClientApplication.readCounter.incrementAndGet();
                    if (receiveData == null) {
                        socket.close();
                    }

                    if (payload.getType().equals(PayloadType.SEND_ACK)) {
                        ClientSendAck sendAck = Jackson.getInstance().getObjectMapper().convertValue(payload.getData(), ClientSendAck.class);
                        if (sendAck.getResult().equals(MessageResult.SUCCESS)) {
                            TcpClientApplication.sendAckSuccessCounter.incrementAndGet();
                        } else {
                            TcpClientApplication.sendAckFailCounter.incrementAndGet();
                        }
                    } else if (payload.getType().equals(PayloadType.REPORT)) {
                        ClientReport report = Jackson.getInstance().getObjectMapper().convertValue(payload.getData(), ClientReport.class);
                        if (report != null && report.getResult().equals(MessageResult.SUCCESS)) {
                            TcpClientApplication.reportSuccessCounter.incrementAndGet();
                        } else {
                            TcpClientApplication.reportFailCounter.incrementAndGet();
                        }
                    } else if (payload.getData() instanceof ClientErrorPayload) {
                        ErrorPayload errorPayload = Jackson.getInstance().getObjectMapper().convertValue(payload.getData(), ErrorPayload.class);
                        System.out.println("reader:error content: [" + errorPayload.getReason() + "]");
                    }

                    if (TcpClientApplication.reportFailCounter.get() + TcpClientApplication.reportSuccessCounter.get() == totalSendCount) {
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
}