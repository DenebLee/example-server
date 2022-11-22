package kr.nanoit;


import kr.nanoit.module.broker.Broker;
import kr.nanoit.module.broker.BrokerImpl;
import kr.nanoit.module.branch.Branch;
import kr.nanoit.module.filter.Filter;
import kr.nanoit.module.inbound.TcpServer;
import kr.nanoit.module.inbound.socket.SocketManager;
import kr.nanoit.module.mapper.Mapper;
import kr.nanoit.module.outbound.OutBound;
import kr.nanoit.module.sender.Sender;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class TcpServerApplication {
    public static int port = 12323;

    public static void main(String[] args) throws IOException, InterruptedException {

        SocketManager socketManager = new SocketManager();
        Broker broker = new BrokerImpl(socketManager);

        Mapper mapper = new Mapper(broker);
        Filter filter = new Filter(broker);
        Branch branch = new Branch(broker);
        Sender sender = new Sender(broker);
        OutBound outBound = new OutBound(broker);
        TcpServer tcpServer = new TcpServer(socketManager, broker, port);


        Thread socketManagerThread = new Thread(socketManager);
        Thread mapperThread = new Thread(mapper);
        Thread filterThread = new Thread(filter);
        Thread branchThread = new Thread(branch);
        Thread senderThread = new Thread(sender);
        Thread outBoundThread = new Thread(outBound);
        Thread tcpserverThread = new Thread(tcpServer);

        socketManagerThread.setDaemon(true);

        socketManagerThread.start();
        mapperThread.start();
        filterThread.start();
        branchThread.start();
        senderThread.start();
        outBoundThread.start();


        tcpserverThread.start();

        System.out.println("==========================================================================================================================================");
        log.info("  ECHO SERVER START  ");
        System.out.println("==========================================================================================================================================");
    }
}