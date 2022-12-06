package kr.nanoit.module.inbound.socket;

import kr.nanoit.module.broker.Broker;
import kr.nanoit.module.inbound.thread.gateway.ReadStreamThread;
import kr.nanoit.module.inbound.thread.carrier.ReceiveFromCarrierThread;
import kr.nanoit.module.inbound.thread.carrier.SendToCarrierThread;
import kr.nanoit.module.inbound.thread.gateway.WriteStreamThread;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Data

// 접속된 클라이언트어ㅣ 1:1 생성
public class SocketResource {
    private final String uuid;
    private final Socket socket;
    private final Socket connectCarrier;
    private final Broker broker;
    private final LinkedBlockingQueue<String> writeBuffer;

    private final Thread readStreamThread;
    private final Thread writeStreamThread;
    private final Thread sendToCarrierThread;
    private final Thread receiveFromCarrierThread;
    private boolean readThreadStatus = true;
    private boolean writeThreadStatus = true;
    private boolean receiveFromCarrierThreadStatus = true;
    private boolean sendToCarrierThreadStatus = true;

    public SocketResource(Socket socket, Socket connectCarrier, Broker broker) throws IOException {
        this.uuid = UUID.randomUUID().toString().substring(0, 7);
        this.socket = socket;
        this.connectCarrier = connectCarrier;
        this.broker = broker;
        this.writeBuffer = new LinkedBlockingQueue<>();

        this.sendToCarrierThread = new Thread(new SendToCarrierThread(this::sendToCarrierThreadCleaner, broker, new DataOutputStream(connectCarrier.getOutputStream()), uuid));
        sendToCarrierThread.setName(uuid + "-send");
        this.receiveFromCarrierThread = new Thread(new ReceiveFromCarrierThread(this::receiveFromCarrierThreadCleaner, broker, new BufferedReader(new InputStreamReader(connectCarrier.getInputStream())), uuid));
        receiveFromCarrierThread.setName(uuid + "-receive");

        this.readStreamThread = new Thread(new ReadStreamThread(this::readThreadCleaner, broker, new BufferedReader(new InputStreamReader(socket.getInputStream())), uuid));
        readStreamThread.setName(uuid + "-read");
        this.writeStreamThread = new Thread(new WriteStreamThread(this::writeThreadCleaner, new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), uuid, writeBuffer));
        writeStreamThread.setName(uuid + "-write");
    }

    public void serve() {
        readStreamThread.start();
        writeStreamThread.start();
        receiveFromCarrierThread.start();
        sendToCarrierThread.start();
    }

    public void write(String payload) {
        if (payload != null) {
            writeBuffer.offer(payload);
        }
    }

    public void readThreadCleaner(String calledClassName) {
        try {
            writeStreamThread.interrupt();
            this.socket.shutdownInput();
            readThreadStatus = false;
        } catch (IOException e) {
            log.error("[@SOCKET:RESOURCE@] key={} SOCKET INPUT STREAM CLOSE FAILED", uuid, e);
        }
    }

    public void writeThreadCleaner(String calledClassName) {
        try {
            readStreamThread.interrupt();
            this.socket.shutdownOutput();
            writeThreadStatus = false;
        } catch (IOException e) {
            log.error("[@SOCKET:RESOURCE@] key={} SOCKET OUT STREAM CLOSE FAILED", uuid, e);
        }

    }


    public void receiveFromCarrierThreadCleaner(String calledClassName) {
        try {
            if (!writeThreadStatus && !readThreadStatus) {
                sendToCarrierThread.interrupt();
                this.connectCarrier.shutdownInput();
                receiveFromCarrierThreadStatus = false;
            }
        } catch (IOException e) {
            log.error("[@SOCKET:RESOURCE@] key={} SOCKET INPUT STREAM CLOSE FAILED", uuid, e);
        }
    }

    public void sendToCarrierThreadCleaner(String calledClassName) {
        try {
            if (!writeThreadStatus && !readThreadStatus) {
            receiveFromCarrierThread.interrupt();
            this.connectCarrier.shutdownOutput();
            sendToCarrierThreadStatus = false;
            }
        } catch (IOException e) {
            log.error("[@SOCKET:RESOURCE@] key={} SOCKET OUT STREAM CLOSE FAILED", uuid, e);
        }

    }


    public boolean isTerminated() {
        return readStreamThread.getState().equals(Thread.State.TERMINATED) && writeStreamThread.getState().equals(Thread.State.TERMINATED)
                && receiveFromCarrierThread.getState().equals(Thread.State.TERMINATED) && sendToCarrierThread.getState().equals(Thread.State.TERMINATED);
    }

    public void connectClose() throws IOException {
        log.warn("[@SOCKET:RESOURCE@] key={} SOCKET CLOSE", uuid);
        socket.close();
        connectCarrier.close();
    }

    public boolean isSocketInputStreamClose() {
        return socket.isInputShutdown() && connectCarrier.isInputShutdown();
    }


    public boolean isSocketOutputStreamClose() {
        return socket.isOutputShutdown() && connectCarrier.isOutputShutdown();
    }
}