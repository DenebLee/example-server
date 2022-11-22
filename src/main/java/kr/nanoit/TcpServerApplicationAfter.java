package kr.nanoit;


import kr.nanoit.module.branch.ThreadBranch;
import kr.nanoit.module.broker.Broker;
import kr.nanoit.module.broker.BrokerImpl;
import kr.nanoit.module.filter.ThreadFilter;
import kr.nanoit.module.inbound.TcpServer;
import kr.nanoit.module.inbound.socket.SocketManager;
import kr.nanoit.module.mapper.ThreadMapper;
import kr.nanoit.module.outbound.ThreadOutBound;
import kr.nanoit.module.sender.ThreadSender;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
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

        Thread socketManagerThread = new Thread(socketManager);
        Thread tcpserverThread = new Thread(new TcpServer(socketManager, broker, port));

        socketManagerThread.setDaemon(true);

        socketManagerThread.start();
        tcpserverThread.start();

        System.out.println("==========================================================================================================================================");
        log.info("  ECHO TEST SERVER START  ");
        System.out.println("==========================================================================================================================================");
    }

    private static String getRandomUuid() {
        return UUID.randomUUID().toString().substring(0, 7);
    }

}