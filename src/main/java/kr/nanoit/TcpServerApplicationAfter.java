package kr.nanoit;


import kr.nanoit.abst.ModuleProcess;
import kr.nanoit.abst.ModuleProcessManagerImpl;
import kr.nanoit.db.DataBaseConfig;
import kr.nanoit.db.PostgreSqlDbcp;
import kr.nanoit.db.auth.MessageService;
import kr.nanoit.module.branch.ThreadBranch;
import kr.nanoit.module.broker.Broker;
import kr.nanoit.module.broker.BrokerImpl;
import kr.nanoit.module.filter.ThreadFilter;
import kr.nanoit.module.inbound.socket.SocketManager;
import kr.nanoit.module.inbound.socket.ThreadTcpServer;
import kr.nanoit.module.mapper.ThreadMapper;
import kr.nanoit.module.outbound.ThreadOutBound;
import kr.nanoit.module.sender.ThreadSender;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.UUID;

@Slf4j
public class TcpServerApplicationAfter {
    public static int port = 12323;

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        SocketManager socketManager = new SocketManager();
        Broker broker = new BrokerImpl(socketManager);
        DataBaseConfig dataBaseConfig = new DataBaseConfig()
                .setIp("192.168.0.64")
                .setPort(5432)
                .setDatabaseName("lee")
                .setUsername("lee")
                .setPassword("lee");
        PostgreSqlDbcp dbcp = new PostgreSqlDbcp(dataBaseConfig);
        MessageService authService = MessageService.createPostgreSqL(dbcp);

        new ThreadMapper(broker, getRandomUuid());
        new ThreadFilter(broker, getRandomUuid());
        new ThreadBranch(broker, getRandomUuid(), authService);
        new ThreadSender(broker, getRandomUuid());
        new ThreadOutBound(broker, getRandomUuid());
        new ThreadTcpServer(socketManager, broker, port, getRandomUuid());

        Thread socketManagerThread = new Thread(socketManager);

        ModuleProcessManagerImpl moduleProcessManagerImpl = ModuleProcess.moduleProcessManagerImpl;

        socketManagerThread.setDaemon(true);
        socketManagerThread.start();

        System.out.println("");
        System.out.println("==========================================================================================================================================");
        System.out.println("                                                     ECHO TEST SERVER START  ");
        System.out.println("==========================================================================================================================================");
        System.out.println("");

    }

    private static String getRandomUuid() {
        return UUID.randomUUID().toString().substring(0, 7);
    }

}