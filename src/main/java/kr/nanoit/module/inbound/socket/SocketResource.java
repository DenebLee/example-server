package kr.nanoit.module.inbound.socket;

import kr.nanoit.module.broker.Broker;
import kr.nanoit.module.inbound.thread.gateway.ReadStreamThread;
import kr.nanoit.module.inbound.thread.gateway.WriteStreamThread;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Data

public class SocketResource {
    private final String uuid;
    private final Socket socket;
    private final Broker broker;
    private final LinkedBlockingQueue<String> writeBuffer;

    private final Thread readStreamThread;
    private final Thread writeStreamThread;
    private boolean readThreadStatus = true;
    private boolean writeThreadStatus = true;

    public SocketResource(Socket socket, Broker broker) throws IOException {
        this.uuid = UUID.randomUUID().toString().substring(0, 7);
        this.socket = socket;
        this.broker = broker;
        this.writeBuffer = new LinkedBlockingQueue<>();
        this.readStreamThread = new Thread(new ReadStreamThread(this::readThreadCleaner, broker, new BufferedReader(new InputStreamReader(socket.getInputStream())), uuid));
        readStreamThread.setName(uuid + "-read");
        this.writeStreamThread = new Thread(new WriteStreamThread(this::writeThreadCleaner, new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), uuid, writeBuffer));
        writeStreamThread.setName(uuid + "-write");

        socket.setSoTimeout(60000);
    }

    public void serve() {
        readStreamThread.start();
        writeStreamThread.start();
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

    public boolean isTerminated() {
        return readStreamThread.getState().equals(Thread.State.TERMINATED) && writeStreamThread.getState().equals(Thread.State.TERMINATED);
    }

    public void connectClose() throws IOException {
        log.warn("[@SOCKET:RESOURCE@] key={} SOCKET CLOSE", uuid);
        socket.close();
    }

    public boolean isSocketInputStreamClose() {
        return socket.isInputShutdown();
    }


    public boolean isSocketOutputStreamClose() {
        return socket.isOutputShutdown();
    }

    public int getWriteBufferQueueSize() {
        return writeBuffer.size();
    }

}