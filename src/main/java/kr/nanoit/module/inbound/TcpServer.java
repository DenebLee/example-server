package kr.nanoit.module.inbound;

import kr.nanoit.module.borker.Broker;
import kr.nanoit.module.inbound.socket.SocketManager;
import kr.nanoit.module.inbound.socket.SocketResource;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
public class TcpServer {

    private final SocketManager socketManager;
    private final Broker broker;
    private final ServerSocket serverSocket;

    public TcpServer(SocketManager socketManager, Broker broker, int port) throws IOException {
        this.socketManager = socketManager;
        this.broker = broker;
        this.serverSocket = new ServerSocket(port);
    }

    public void serve()  {
        Thread socketServerThread = new Thread(() -> {
            try {
                while (true) {
                    Socket socket = serverSocket.accept();
                    SocketResource socketResource = new SocketResource(socket, broker);
                    log.info("[SERVER:SOCKET:{}] accept: remote={}", socketResource.getUuid(), socket.getRemoteSocketAddress().toString());
                    socketManager.register(socketResource);
                    socketResource.serve();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        socketServerThread.setDaemon(true);
        socketServerThread.start();
    }

    public void close() throws IOException {
        serverSocket.close();
    }
}
