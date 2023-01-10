package kr.nanoit.module.carrier;

import kr.nanoit.db.DataBaseConfig;
import kr.nanoit.db.PostgreSqlDbcp;
import kr.nanoit.db.auth.MessageService;
import kr.nanoit.db.auth.MessageServiceImpl;
import kr.nanoit.domain.entity.AgentEntity;
import kr.nanoit.domain.entity.MemberEntity;
import kr.nanoit.domain.message.AgentStatus;
import kr.nanoit.module.broker.Broker;
import kr.nanoit.module.broker.BrokerImpl;
import kr.nanoit.module.inbound.socket.SocketManager;
import kr.nanoit.module.sender.ThreadSender;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;


@Testcontainers
class ThreadCarrierTest {
    private static DataBaseConfig dataBaseConfig;
    private ThreadCarrier threadCarrier;
    private static SocketManager socketManager;
    private Thread carrierThread;
    private static Broker broker;
    private String uuid;
    private static MessageService messageService;
    private static PostgreSqlDbcp dbcp;

    @Container
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:14.5-alpine")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test");

    @BeforeAll
    static void beforeAll() throws ClassNotFoundException, URISyntaxException, IOException {
        socketManager = mock(SocketManager.class);
        dataBaseConfig = new DataBaseConfig();
        dataBaseConfig.setIp(postgreSQLContainer.getHost())
                .setPort(postgreSQLContainer.getFirstMappedPort())
                .setDatabaseName(postgreSQLContainer.getDatabaseName())
                .setUsername(postgreSQLContainer.getUsername())
                .setPassword(postgreSQLContainer.getPassword());

        dbcp = new PostgreSqlDbcp(dataBaseConfig);
        messageService = new MessageServiceImpl(dbcp);
        broker = spy(new BrokerImpl(socketManager));
        dbcp.initSchema();

        // Table dependency data injection
        messageService.insertAccessList(2, "192.168.0.16");
        messageService.insertAgentStatus("CONNECTED", "DISCONNECTED");
        messageService.insertMessageType("AUTHENTICATION");
        messageService.insertMessageStatus("SENT", "RECEIVE");

        MemberEntity memberEntity = new MemberEntity(0, "이정섭", "$2a$12$9aqZtS4tclIN.sq3/J8qGuavmarzH5q5.z0Qz.7coXzD1MLjf0zRG", "test@test.com", new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()));
        messageService.insertUser(memberEntity);

        AgentEntity agentEntity = new AgentEntity(1, 1, 2, AgentStatus.DISCONNECTED, new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()));
        messageService.insertAgent(agentEntity);
    }

    @BeforeEach
    void setUp() {
        this.uuid = UUID.randomUUID().toString();
        this.threadCarrier = spy(new ThreadCarrier(broker, uuid, messageService));
        this.carrierThread = spy(new Thread(threadCarrier));
    }
}