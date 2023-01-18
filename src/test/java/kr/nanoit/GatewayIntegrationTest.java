package kr.nanoit;

import kr.nanoit.abst.ModuleProcess;
import kr.nanoit.abst.ModuleProcessManagerImpl;
import kr.nanoit.db.DataBaseConfig;
import kr.nanoit.db.PostgreSqlDbcp;
import kr.nanoit.db.auth.MessageService;
import kr.nanoit.db.auth.MessageServiceImpl;
import kr.nanoit.domain.entity.AgentEntity;
import kr.nanoit.domain.entity.MemberEntity;
import kr.nanoit.domain.message.AgentStatus;
import kr.nanoit.domain.payload.*;
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
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

@Testcontainers
class GatewayIntegrationTest {

    private static SocketManager socketManager;
    private static UserManager userManager;
    private static Broker broker;
    private static DataBaseConfig dataBaseConfig;
    private static MessageService messageService;
    private static TestClient testClient;


    private Thread socketManagerThread;
    private Thread userManagerThread;
    private Thread clientThread;

    private static PostgreSqlDbcp dbcp;

    private int port;
    private static ObjectMapper objectMapper;

    @Mock


    @Container
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:14.5-alpine")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test");

    @BeforeAll
    static void beforeAll() throws ClassNotFoundException, URISyntaxException, IOException {
        socketManager = spy(new SocketManager());
        userManager = spy(new UserManager(socketManager, messageService));

        broker = spy(new BrokerImpl(socketManager));
        dataBaseConfig = new DataBaseConfig()
                .setIp(postgreSQLContainer.getHost())
                .setPort(postgreSQLContainer.getFirstMappedPort())
                .setDatabaseName(postgreSQLContainer.getDatabaseName())
                .setUsername(postgreSQLContainer.getUsername())
                .setPassword(postgreSQLContainer.getPassword());
        dbcp = spy(new PostgreSqlDbcp(dataBaseConfig));
        messageService = spy(new MessageServiceImpl(dbcp));


        dbcp.initSchema();


        // Table dependency data injection
        messageService.insertAccessList(1, "192.168.0.16");
        messageService.insertAgentStatus("CONNECTED", "DISCONNECTED");
        messageService.insertMessageType("AUTHENTICATION");
        messageService.insertMessageType("SEND");
        messageService.insertMessageType("REPORT_ACK");
        messageService.insertMessageStatus("SENT", "RECEIVE");
        messageService.insertRelayCompany();
        // id 1로 고정 단 하나

        MemberEntity memberEntity = new MemberEntity(0, "이정섭", "$2a$12$9aqZtS4tclIN.sq3/J8qGuavmarzH5q5.z0Qz.7coXzD1MLjf0zRG", "test@test.com", new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()));
        messageService.insertUser(memberEntity);

        MemberEntity memberEntity2 = new MemberEntity(1, "양선호", "$2a$12$TA9gfrHXvAnrcQjeCUHPT.aJAK3d.wGdAVeBO.SedfKOAoHys6hpS", "test@test.com", new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()));
        messageService.insertUser(memberEntity2);

        AgentEntity agentEntity = new AgentEntity(0, 1, 1, AgentStatus.DISCONNECTED, new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()));
        messageService.insertAgent(agentEntity);

        AgentEntity agentEntity2 = new AgentEntity(1, 2, 1, AgentStatus.DISCONNECTED, new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()));
        messageService.insertAgent(agentEntity2);


        objectMapper = new ObjectMapper();
    }


    @BeforeEach
    void setUp() throws IOException {
        port = getRandomPort();
        spy(new ThreadMapper(broker, getRandomUuid()));
        spy(new ThreadFilter(broker, getRandomUuid(), userManager));
        spy(new ThreadBranch(broker, getRandomUuid(), messageService, userManager));
        spy(new ThreadSender(broker, getRandomUuid(), messageService, userManager));
        spy(new ThreadOutBound(broker, getRandomUuid(), socketManager, userManager));
        spy(new ThreadCarrier(broker, getRandomUuid(), messageService));
        spy(new ThreadTcpServer(socketManager, broker, port, getRandomUuid()));

        ModuleProcessManagerImpl moduleProcessManager = ModuleProcess.moduleProcessManagerImpl;

        testClient = spy(new TestClient(port));
        userManagerThread = spy(new Thread(userManager));
        socketManagerThread = spy(new Thread(socketManager));
        clientThread = spy(new Thread(testClient));

        userManagerThread.start();
        socketManagerThread.start();
        clientThread.start();
    }

    @AfterEach
    void tearDown() {
        userManagerThread.interrupt();
        socketManagerThread.interrupt();
        clientThread.interrupt();

        messageService.updateAgentStatus(1, 1, AgentStatus.DISCONNECTED, new Timestamp(System.currentTimeMillis()));
        messageService.deleteCompanyMessageTable();
        messageService.deleteClientMessageTable();
        testClient.deleteList();
    }

    @DisplayName("Client가 인증 메세지를 전송 하였을 때 Server는 정상 처리 되어 Authentication_Ack를 던져줘야 한다")
    @Test
    void t1() throws IOException, InterruptedException {
        // given
        String uuid = getRandomUuid();
        Authentication authentication = new Authentication(1, "이정섭", "이정섭", "test@test.com");
        Payload expected = new Payload(PayloadType.AUTHENTICATION, uuid, authentication);
        String sendData = makeString(expected);

        // when
        testClient.write(sendData, 1);
        Thread.sleep(1000);

        // then
        String value = testClient.getResponseData();
        assertThat(value).isNotNull();
        Payload payload = objectMapper.readValue(value, Payload.class);
        assertThat(payload.getType()).isEqualTo(PayloadType.AUTHENTICATION_ACK);
        assertThat(payload.getMessageUuid()).isEqualTo(uuid);
        AuthenticationAck authenticationAck = objectMapper.convertValue(payload.getData(), AuthenticationAck.class);
        assertThat(authenticationAck.getAgent_id()).isEqualTo(authentication.getAgent_id());
        assertThat(authenticationAck.getResult()).isEqualTo("Authentication Success");
    }

    @DisplayName("인증이 완료된 상태에서 Clinet가 보낸 Message가 정상 처리되어 Db에 저장되고 ack를 던져줘야 한다 -> 갯수 1개")
    @Test
    void t2() throws IOException, InterruptedException {
        // given,when
        testClient.writeWithAuth(1);
        Thread.sleep(3000);

        // then
        int actual = messageService.getCountMessageList();
        assertThat(actual).isEqualTo(1);
        assertThat(testClient.getResponseData().length()).isEqualTo(2);
    }


    private static String getRandomUuid() {
        return UUID.randomUUID().toString();
    }

    private String makeString(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }

    private String makeSendData(Send send, String uuid) throws JsonProcessingException {
        Payload expected = new Payload(PayloadType.SEND, uuid, send);
        return makeString(expected);
    }

    private int getRandomPort() {
        return new SecureRandom().nextInt(64511) + 1024;
    }
}