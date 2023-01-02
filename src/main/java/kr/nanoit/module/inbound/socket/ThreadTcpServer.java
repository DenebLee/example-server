package kr.nanoit.module.inbound.socket;

import kr.nanoit.abst.ModuleProcess;
import kr.nanoit.module.broker.Broker;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

@Slf4j
public class ThreadTcpServer extends ModuleProcess {

    private final SocketManager socketManager;
    private boolean flag;
    private final int port;
    private final UserManager userManager;

    public ThreadTcpServer(SocketManager socketManager, Broker broker, int port, String uuid, UserManager userManager) {
        super(broker, uuid);
        this.socketManager = socketManager;
        this.port = port;
        this.userManager = userManager;
    }


    @Override
    public void run() {
        try {
            flag = true;
            ServerSocket serverSocket = new ServerSocket(this.port);
            while (flag) {
                Socket socket = serverSocket.accept();
                SocketResource socketResource = new SocketResource(socket, broker, userManager);
                log.info("[TCPSERVER : SOCKET : {}] ACCEPT => ADDRESS = {}", socketResource.getUuid(), socket.getRemoteSocketAddress().toString());
                if (socketManager.register(socketResource)) {
                    socketResource.serve();
                }
            }
        } catch (ConnectException e) {
            log.error("[TCPSERVER : SOCKET : {}]  ERROR WHILE CONNECT = {}  ", uuid, e.getMessage());
        } catch (SocketTimeoutException e) {
            log.error("[TCPSERVER : SOCKET : {}]  CONNECTION = {} ", uuid, e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void shoutDown() {
        flag = false;
    }

    @Override
    public void sleep() throws InterruptedException {
        Thread.sleep(1000);
    }

    @Override
    public String getUuid() {
        return this.uuid;
    }
}
