package kr.nanoit;


import kr.nanoit.abst.ThreadManagerUseAbstract;
import kr.nanoit.module.branch.ThreadBranch;
import kr.nanoit.module.broker.Broker;
import kr.nanoit.module.broker.BrokerImpl;
import kr.nanoit.module.filter.ThreadFilter;
import kr.nanoit.module.inbound.socket.SocketManager;
import kr.nanoit.module.inbound.socket.SocketResource;
import kr.nanoit.module.inbound.socket.ThreadTcpServer;
import kr.nanoit.module.mapper.ThreadMapper;
import kr.nanoit.module.outbound.ThreadOutBound;
import kr.nanoit.module.sender.ThreadSender;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.UUID;

@Slf4j
public class TcpServerApplicationAfter {
    public static int port = 12323;

    public static void main(String[] args) throws IOException {

        SocketManager socketManager = new SocketManager();
        Broker broker = new BrokerImpl(socketManager);

        new ThreadMapper(broker, getRandomUuid());
        new ThreadFilter(broker, getRandomUuid());
        new ThreadBranch(broker, getRandomUuid());
        new ThreadSender(broker, getRandomUuid());
        new ThreadOutBound(broker, getRandomUuid());
        new ThreadTcpServer(socketManager, broker, port, getRandomUuid());

        Thread socketManagerThread = new Thread(socketManager);

        ThreadManagerUseAbstract threadManagerUseAbstract = new ThreadManagerUseAbstract();
        threadManagerUseAbstract.monitor();

        socketManagerThread.setDaemon(true);
        socketManagerThread.start();

        System.out.println("==========================================================================================================================================");
        log.info("  ECHO TEST SERVER START  ");
        System.out.println("==========================================================================================================================================");
    }

    private static String getRandomUuid() {
        return UUID.randomUUID().toString().substring(0, 7);
    }

}