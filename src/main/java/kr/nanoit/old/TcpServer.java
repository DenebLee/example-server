package kr.nanoit.old;

import kr.nanoit.module.broker.Broker;
import kr.nanoit.module.inbound.socket.SocketManager;
import kr.nanoit.module.inbound.socket.SocketResource;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Slf4j
public class TcpServer implements Process {

    private final SocketManager socketManager;
    private final Broker broker;
    private final ServerSocket serverSocket;
    private boolean flag;
    private Instant start;
    private Instant finish;

    public TcpServer(SocketManager socketManager, Broker broker, int port) throws IOException {
        this.socketManager = socketManager;
        this.broker = broker;
        this.serverSocket = new ServerSocket(port);
    }


    @Override
    public void run() {
        try {
            flag = true;
            while (flag) {
                Socket socket = serverSocket.accept();
                SocketResource socketResource = new SocketResource(socket, broker);
                log.info("[TCPSERVER : SOCKET : {}] ACCEPT => ADDRESS = {}", socketResource.getUuid().substring(0, 7), socket.getRemoteSocketAddress().toString());
                socketManager.register(socketResource);
                socketResource.serve();
            }
            finish = Instant.now();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connectClose() throws IOException {
        serverSocket.close();
    }

    public void shutDown() {
        flag = false;
    }

    @Override
    public String getUuid() {
        return UUID.randomUUID().toString().substring(0, 7);
    }

    @Override
    public boolean getFlag() {
        return this.flag;
    }

    @Override
    public long getRunningTime() {
        return Duration.between(start, finish).toMillis();
    }
}
