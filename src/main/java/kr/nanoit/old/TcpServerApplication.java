package kr.nanoit.old;


import kr.nanoit.module.broker.Broker;
import kr.nanoit.module.broker.BrokerImpl;
import kr.nanoit.module.inbound.socket.SocketManager;
import kr.nanoit.thread.ThreadManager;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class TcpServerApplication {
    public static int port = 12323;

    public static void main(String[] args) throws IOException {

        SocketManager socketManager = new SocketManager();
        Broker broker = new BrokerImpl(socketManager);

        Thread socketManagerThread = new Thread(socketManager);
        socketManagerThread.setDaemon(true);

        ThreadManager threadManager = new ThreadManager(new Mapper(broker), new Filter(broker), new Branch(broker), new Sender(broker), new OutBound(broker), new TcpServer(socketManager, broker, port));

        socketManagerThread.start();
        threadManager.monitor();

//        threadManager.register()
//        threadManager.unregister()

        System.out.println("==========================================================================================================================================");
        log.info("  ECHO SERVER START  ");
        System.out.println("==========================================================================================================================================");

    }
}