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
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
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


    private Thread socketManagerThread;
    private Thread userManagerThread;

    private static PostgreSqlDbcp dbcp;

    private static TestClient testClient;

    private int port;
    private static ObjectMapper objectMapper;


    @Container
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:14.5-alpine")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test");

    @BeforeAll
    static void beforeAll() throws ClassNotFoundException, URISyntaxException, IOException {
        socketManager = spy(new SocketManager());
        userManager = spy(new UserManager(socketManager,messageService));

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

        testClient = new TestClient();

        objectMapper = new ObjectMapper();
    }


    @BeforeEach
    void setUp() {
        port = getRandomPort();
        spy(new ThreadMapper(broker, getRandomUuid()));
        spy(new ThreadFilter(broker, getRandomUuid(), userManager));
        spy(new ThreadBranch(broker, getRandomUuid(), messageService, userManager));
        spy(new ThreadSender(broker, getRandomUuid(), messageService));
        spy(new ThreadOutBound(broker, getRandomUuid()));
        spy(new ThreadCarrier(broker, getRandomUuid(), messageService));
        spy(new ThreadTcpServer(socketManager, broker, port, getRandomUuid(), userManager));

        ModuleProcessManagerImpl moduleProcessManager = ModuleProcess.moduleProcessManagerImpl;
        userManagerThread = spy(new Thread(userManager));
        socketManagerThread = spy(new Thread(socketManager));

        userManagerThread.start();
        socketManagerThread.start();
    }

    @AfterEach
    void tearDown() throws IOException, InterruptedException {
        Thread.sleep(500);

        userManagerThread.interrupt();
        socketManagerThread.interrupt();
        testClient.stop();
    }

    @DisplayName("client가 접속하면 socketResource가 생성 되고 SocketManager에 Regist 가 되어야 한다")
    @Test
    void t1() throws InterruptedException {
        // given, when
        testClient.connect(port);
        testClient.delay();

        // then
        assertThat(socketManager.getSocketResourcesMapSize()).isEqualTo(1);
    }

    @DisplayName("client가 접속하고 인증 메시지를 5초 동안 보내지 않으면 인증 실패 처리가 되며 연결이 끊겨야 한다")
    @Test
    void t2() throws InterruptedException {
        // given , when
        testClient.connect(port);
        testClient.delay(); // -> 5초
        Thread.sleep(1000);

        assertThat(testClient.socket.isClosed()).isTrue();

    }

    @DisplayName("client가 올바른 인증 메시지를 요청하였을때 서버는 성공 여부를 전달 하여야 한다")
    @Test
    void t3() throws IOException, InterruptedException {
        // given
        String uuid = getRandomUuid();
        Authentication authentication = new Authentication(1, "이정섭", "이정섭", "test@test.com");
        Payload expected = new Payload(PayloadType.AUTHENTICATION, uuid, authentication);
        String sendData = objectMapper.writeValueAsString(expected);

        // when
        testClient.connect(port);
        testClient.write(sendData, 1);

        // then
        String actual = testClient.read(1);
        assertThat(actual).isNotNull();

        Payload payload = objectMapper.readValue(actual, Payload.class);
        assertThat(payload.getType()).isEqualTo(PayloadType.AUTHENTICATION_ACK);
        assertThat(payload.getMessageUuid()).isEqualTo(uuid);
        AuthenticationAck authenticationAck = objectMapper.convertValue(payload.getData(), AuthenticationAck.class);
        assertThat(authenticationAck.getAgent_id()).isEqualTo(authentication.getAgent_id());
        assertThat(authenticationAck.getResult()).isEqualTo("Authentication Success");

    }

    @DisplayName("client가 유저 정보가 틀린 인증 메시지를 요청하였을 때 서버는 실패 여부를 전달 하여야 한다")
    @Test
    void t4() throws IOException, InterruptedException {
        // given
        String uuid = getRandomUuid();
        Authentication authentication = new Authentication(3, "손흥민", "손흥민", "test@test.com");
        Payload expected = new Payload(PayloadType.AUTHENTICATION, uuid, authentication);
        String sendData = objectMapper.writeValueAsString(expected);

        // when
        testClient.connect(port);
        testClient.write(sendData, 1);

        // then
        String actual = testClient.read(1);
        assertThat(actual).isNotNull();

        Payload payload = objectMapper.readValue(actual, Payload.class);
        assertThat(payload.getType()).isEqualTo(PayloadType.AUTHENTICATION_ACK);
        assertThat(payload.getMessageUuid()).isEqualTo(uuid);
        ErrorPayload errorPayload = objectMapper.convertValue(payload.getData(), ErrorPayload.class);
        assertThat(errorPayload.getReason()).isEqualTo("Authentication failure Account information verification required");
    }

    @DisplayName("client가 비밀번호가 틀린 인증 메세지를 요청하였을 때 서버는 실패 여부를 전달 하여야 한다")
    @Test
    void t5() throws IOException, InterruptedException {
        // given
        String uuid = getRandomUuid();
        Authentication authentication = new Authentication(1, "이정섭", "양선호", "test@test.com");
        Payload expected = new Payload(PayloadType.AUTHENTICATION, uuid, authentication);
        String sendData = objectMapper.writeValueAsString(expected);

        // when
        testClient.connect(port);
        testClient.write(sendData, 1);

        // then
        String actual = testClient.read(1);
        assertThat(actual).isNotNull();

        Payload payload = objectMapper.readValue(actual, Payload.class);
        assertThat(payload.getType()).isEqualTo(PayloadType.AUTHENTICATION_ACK);
        assertThat(payload.getMessageUuid()).isEqualTo(uuid);
        ErrorPayload errorPayload = objectMapper.convertValue(payload.getData(), ErrorPayload.class);
        assertThat(errorPayload.getReason()).isEqualTo("Failed to Authentication");
    }

    @DisplayName("client가 agent_id가 틀린 인증 메세지를 요청하였을 떄 서버는 실패 여부를 전달 하여야 한다")
    @Test
    void t6() throws IOException, InterruptedException {
        // given
        String uuid = getRandomUuid();
        Authentication authentication = new Authentication(3, "이정섭", "이정섭", "test@test.com");
        Payload expected = new Payload(PayloadType.AUTHENTICATION, uuid, authentication);
        String sendData = objectMapper.writeValueAsString(expected);

        // when
        testClient.connect(port);
        testClient.write(sendData, 1);

        // then
        String actual = testClient.read(1);
        assertThat(actual).isNotNull();

        Payload payload = objectMapper.readValue(actual, Payload.class);
        assertThat(payload.getType()).isEqualTo(PayloadType.AUTHENTICATION_ACK);
        assertThat(payload.getMessageUuid()).isEqualTo(uuid);
        ErrorPayload errorPayload = objectMapper.convertValue(payload.getData(), ErrorPayload.class);
        assertThat(errorPayload.getReason()).isEqualTo("Agent does not exist");
    }

    @DisplayName("client가 agent 접속 상태가 온라인 상태인 인증 메세지를 요청 하였을 때 서버는 실패 여부를 전달 하여야 한다 ")
    @Test
    void t7() throws IOException, InterruptedException {
        // given
        AgentEntity agentEntity = new AgentEntity(3, 1, 1, AgentStatus.CONNECTED, new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()));
        messageService.insertAgent(agentEntity);

        String uuid = getRandomUuid();
        Authentication authentication = new Authentication(3, "이정섭", "이정섭", "test@test.com");
        Payload expected = new Payload(PayloadType.AUTHENTICATION, uuid, authentication);
        String sendData = objectMapper.writeValueAsString(expected);


        // when
        testClient.connect(port);
        testClient.write(sendData, 1);

        // then
        String actual = testClient.read(1);
        assertThat(actual).isNotNull();

        Payload payload = objectMapper.readValue(actual, Payload.class);
        assertThat(payload.getType()).isEqualTo(PayloadType.AUTHENTICATION_ACK);
        assertThat(payload.getMessageUuid()).isEqualTo(uuid);
        ErrorPayload errorPayload = objectMapper.convertValue(payload.getData(), ErrorPayload.class);
        assertThat(errorPayload.getReason()).isEqualTo("This Agent already connected");
    }

    @DisplayName("client의 인증이 성공 하였을 때 UserInfo Dto가 생성되어 usermanager에 register 되어야 한다 ")
    @Test
    void t8() throws IOException, InterruptedException {
        // given
        String uuid = getRandomUuid();
        Authentication authentication = new Authentication(1, "이정섭", "이정섭", "test@test.com");
        Payload expected = new Payload(PayloadType.AUTHENTICATION, uuid, authentication);
        String sendData = objectMapper.writeValueAsString(expected);

        // when
        testClient.connect(port);
        testClient.write(sendData, 1);

        // then
        assertThat(userManager.getUserResourceMapSize()).isEqualTo(1);
    }

    @DisplayName("client가 메시지를 전송하면 해당 메시지를 정상적으로 처리 한다음 보낸 갯수 만큼 DB에 저장 되어야 한다")
    @Test
    void t9() throws IOException, InterruptedException {

        // given
        String uuid = getRandomUuid();

        Payload payload = new Payload(PayloadType.SEND, uuid, new Send(1, "010-4444-5555", "053-444-5555", "이정섭", "테스트"));
        String sendData = objectMapper.writeValueAsString(payload);

        // when
        testClient.connect(port);
        testClient.writeWithAuthenticaion(100, sendData);
        Thread.sleep(1000);

        // then
        int actual = messageService.getCountMessageList();
        assertThat(actual).isEqualTo(100);
    }


    @DisplayName("")
    @Test
    void t10() throws IOException, InterruptedException {
        // given
        String uuid = getRandomUuid();
        Payload expected = new Payload(PayloadType.SEND, uuid, new Send());
        String sendData = objectMapper.writeValueAsString(expected);

        // when
        testClient.connect(port);
        testClient.write(sendData, 1000);

        // then

    }

    @DisplayName("")
    @Test
    void t11() throws IOException, InterruptedException {
        // given
        String uuid = getRandomUuid();
        Payload expected = new Payload(PayloadType.SEND, uuid, new Send());
        String sendData = objectMapper.writeValueAsString(expected);

        // when
        testClient.connect(port);
        testClient.write(sendData, 1000);

        // then

    }

    @DisplayName("")
    @Test
    void t12() throws IOException, InterruptedException {
        // given
        String uuid = getRandomUuid();
        Payload expected = new Payload(PayloadType.SEND, uuid, new Send());
        String sendData = objectMapper.writeValueAsString(expected);

        // when
        testClient.connect(port);
        testClient.write(sendData, 1000);

        // then

    }

    @DisplayName("")
    @Test
    void t13() throws IOException, InterruptedException {
        // given
        String uuid = getRandomUuid();
        Payload expected = new Payload(PayloadType.SEND, uuid, new Send());
        String sendData = objectMapper.writeValueAsString(expected);

        // when
        testClient.connect(port);
        testClient.write(sendData, 1000);

        // then

    }

    @DisplayName("")
    @Test
    void t14() throws IOException, InterruptedException {
        // given
        String uuid = getRandomUuid();
        Payload expected = new Payload(PayloadType.SEND, uuid, new Send());
        String sendData = objectMapper.writeValueAsString(expected);

        // when
        testClient.connect(port);
        testClient.write(sendData, 1000);

        // then

    }


    private static String getRandomUuid() {
        return UUID.randomUUID().toString().substring(0, 7);
    }

    private int getRandomPort() {
        return new SecureRandom().nextInt(64511) + 1024;
    }
}