package kr.nanoit.module.inbound.socket;

import kr.nanoit.module.broker.Broker;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@Data
public class SocketResource {
    private final String uuid;
    private final Socket socket;
    private final Broker broker;
    private final SocketResourceThread socketResourceThread;
    private final LinkedBlockingQueue<String> writeBuffer;

    public SocketResource(Socket socket, Broker broker) throws IOException {
        this.uuid = UUID.randomUUID().toString();
        this.socket = socket;
        this.broker = broker;
        this.writeBuffer = new LinkedBlockingQueue<>();
        this.socketResourceThread = new SocketResourceThread(uuid, broker, writeBuffer, socket);
    }

    public void serve() {
        socketResourceThread.start();
    }

    public void write(String payload) {
        if (payload != null) {
            writeBuffer.offer(payload);
        }
    }

    public boolean isTerminated() {
        return socketResourceThread.isTerminated() && socket.isClosed();
    }
}