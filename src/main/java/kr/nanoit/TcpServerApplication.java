package kr.nanoit;


import kr.nanoit.abst.ModuleProcess;
import kr.nanoit.abst.ModuleProcessManagerImpl;
import kr.nanoit.db.DataBaseConfig;
import kr.nanoit.db.PostgreSqlDbcp;
import kr.nanoit.db.auth.MessageService;
import kr.nanoit.module.branch.ThreadBranch;
import kr.nanoit.module.broker.Broker;
import kr.nanoit.module.broker.BrokerImpl;
import kr.nanoit.module.carrier.ThreadCarrier;
import kr.nanoit.module.filter.ThreadFilter;
import kr.nanoit.module.inbound.socket.SocketManager;
import kr.nanoit.module.inbound.socket.ThreadTcpServer;
import kr.nanoit.module.inbound.socket.UserManager;
import kr.nanoit.module.mapper.ThreadMapper;
import kr.nanoit.module.outbound.ThreadOutBound;
import kr.nanoit.module.sender.ThreadSender;
import kr.nanoit.scheduler.Executor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public class TcpServerApplication {
    public static int port = 12323;

    public static void main(String[] args) throws ClassNotFoundException {

        SocketManager socketManager = new SocketManager();
        UserManager userManager = new UserManager(socketManager);
        Broker broker = new BrokerImpl(socketManager);
        DataBaseConfig dataBaseConfig = new DataBaseConfig()
                .setIp("localhost")
                .setPort(5432)
                .setDatabaseName("test")
                .setUsername("test")
                .setPassword("lee");
        PostgreSqlDbcp dbcp = new PostgreSqlDbcp(dataBaseConfig);
        MessageService messageService = MessageService.createPostgreSqL(dbcp);

        Executor executor = new Executor();
        executor.startExecutor();


        new ThreadMapper(broker, getRandomUuid());
        new ThreadFilter(broker, getRandomUuid(), userManager);
        new ThreadBranch(broker, getRandomUuid(), messageService, userManager);
        new ThreadSender(broker, getRandomUuid(), messageService);
        new ThreadOutBound(broker, getRandomUuid());
        new ThreadCarrier(broker, getRandomUuid(), messageService);

        new ThreadTcpServer(socketManager, broker, port, getRandomUuid(), userManager);

        Thread socketManagerThread = new Thread(socketManager);
        Thread userManagerThread = new Thread(userManager);

        ModuleProcessManagerImpl moduleProcessManagerImpl = ModuleProcess.moduleProcessManagerImpl;


        socketManagerThread.setDaemon(true);
        userManagerThread.setDaemon(true);
        socketManagerThread.start();
        userManagerThread.start();

        System.out.println("");
        System.out.println("====================================================================================================================================================================================");
        System.out.println("                                                                       ECHO TEST SERVER START  ");
        System.out.println("=============================================================================================================================================================================");
        System.out.println("");

    }

    private static String getRandomUuid() {
        return UUID.randomUUID().toString().substring(0, 7);
    }

}