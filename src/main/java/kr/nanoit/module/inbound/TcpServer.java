package kr.nanoit.module.inbound;

import kr.nanoit.common.exception.ReadException;
import kr.nanoit.module.broker.Broker;
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
    private boolean flag = true;

    public TcpServer(SocketManager socketManager, Broker broker, int port) throws IOException {
        this.socketManager = socketManager;
        this.broker = broker;
        this.serverSocket = new ServerSocket(port);
    }

    public void serve() {
        Thread socketServerThread = new Thread(() -> {
            try {
                while (flag) {
                    Socket socket = serverSocket.accept();
                    SocketResource socketResource = new SocketResource(socket, broker);
                    log.info("[TCPSERVER : SOCKET : {}] ACCEPT => ADDRESS = {}", socketResource.getUuid().substring(0, 7), socket.getRemoteSocketAddress().toString());
                    socketManager.register(socketResource);
                    socketResource.serve();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ReadException e) {
                log.error(e.getReason());
                socketManager.terminatedSocketMap(e.getUuid());
                socketManager.getSocketResourcesSize();
            }
        });

        socketServerThread.setDaemon(true);
        socketServerThread.start();
    }

    public void connectClose() throws IOException {
        serverSocket.close();
    }

    public void shutDown() {
        flag = false;
    }
}
