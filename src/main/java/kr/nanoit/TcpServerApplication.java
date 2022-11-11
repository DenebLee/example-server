package kr.nanoit;


import kr.nanoit.module.borker.Broker;
import kr.nanoit.module.borker.BrokerImpl;
import kr.nanoit.module.inbound.TcpServer;
import kr.nanoit.module.inbound.socket.SocketManager;
import kr.nanoit.module.mapper.Mapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class TcpServerApplication {

    public static void main(String[] args) throws IOException, InterruptedException {

        Broker broker = new BrokerImpl();
        SocketManager socketManager = new SocketManager();

        Mapper mapper = new Mapper(broker);

        Thread mapperThread = new Thread(mapper);
        mapperThread.setDaemon(true);
        mapperThread.start();

        TcpServer tcpServer = new TcpServer(socketManager, broker, 12323);
        tcpServer.serve();
        Thread.sleep(999999999);
    }
}