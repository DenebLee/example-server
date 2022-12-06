package kr.nanoit.module.inbound.socket;

import kr.nanoit.abst.ModuleProcess;
import kr.nanoit.module.broker.Broker;
import kr.nanoit.module.inbound.socket.SocketManager;
import kr.nanoit.module.inbound.socket.SocketResource;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.*;

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
                Socket connectCarrier = new Socket();

                // Client 와 Socket connect 이 되면 통신사와 Socket 연결 할 수 있도록 Socket 생성
                if (socket.isConnected()) {
                    connectCarrier.connect(new InetSocketAddress("localhost", 54321));
                    System.out.println("소켓 연결 ");
                }

                SocketResource socketResource = new SocketResource(socket, connectCarrier, broker);
                log.info("[TCPSERVER : SOCKET : {}] ACCEPT => ADDRESS = {}", socketResource.getUuid(), socket.getRemoteSocketAddress().toString());
                socketManager.register(socketResource);
                socketResource.serve();
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
