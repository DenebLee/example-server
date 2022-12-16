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
    private final ServerSocket serverSocket;
    private boolean flag;

    public ThreadTcpServer(SocketManager socketManager, Broker broker, int port, String uuid) throws IOException {
        super(broker, uuid);
        this.socketManager = socketManager;
        this.serverSocket = new ServerSocket(port);
    }


    @Override
    public void run() {
        try {
            flag = true;

            while (flag) {
                Socket socket = serverSocket.accept();
                SocketResource socketResource = new SocketResource(socket, broker);
                log.info("[TCPSERVER : SOCKET : {}] ACCEPT => ADDRESS = {}", socketResource.getUuid(), socket.getRemoteSocketAddress().toString());
                if (socketManager.register(socketResource)) {
                    socketResource.serve();
                }
            }
        } catch (ConnectException e) {
            log.error("[TCPSERVER : SOCKET : {}]  ERROR WHILE CONNECT = {}  ", uuid, e.getMessage());
            this.tryToReconnect();
        } catch (SocketTimeoutException e) {
            log.error("[TCPSERVER : SOCKET : {}]  CONNECTION = {} ", uuid, e.getMessage());
            this.tryToReconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void tryToReconnect() {
        System.out.println("retry !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }

    public void connectClose() throws IOException {
        serverSocket.close();
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
