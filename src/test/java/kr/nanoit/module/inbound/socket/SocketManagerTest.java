package kr.nanoit.module.inbound.socket;

import kr.nanoit.module.broker.Broker;
import kr.nanoit.old.TcpServer;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.spy;

class SocketManagerTest {

    private TcpServer tcpServer;
    private Broker broker;
    private SocketManager socketManager;

    @BeforeEach
    void setUp() {
        this.tcpServer = spy(TcpServer.class);
        this.broker = spy(Broker.class);
        this.socketManager = spy(SocketManager.class);
    }
}