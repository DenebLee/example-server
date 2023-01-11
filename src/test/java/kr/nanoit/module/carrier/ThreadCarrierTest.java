package kr.nanoit.module.carrier;

import kr.nanoit.db.DataBaseConfig;
import kr.nanoit.db.PostgreSqlDbcp;
import kr.nanoit.db.auth.MessageService;
import kr.nanoit.db.auth.MessageServiceImpl;
import kr.nanoit.domain.broker.InternalDataCarrier;
import kr.nanoit.domain.broker.InternalDataOutBound;
import kr.nanoit.domain.broker.InternalDataType;
import kr.nanoit.domain.broker.MetaData;
import kr.nanoit.domain.entity.AgentEntity;
import kr.nanoit.domain.entity.ClientMessageEntity;
import kr.nanoit.domain.entity.MemberEntity;
import kr.nanoit.domain.message.AgentStatus;
import kr.nanoit.domain.message.MessageResult;
import kr.nanoit.domain.message.MessageStatus;
import kr.nanoit.domain.payload.ErrorPayload;
import kr.nanoit.domain.payload.Payload;
import kr.nanoit.domain.payload.PayloadType;
import kr.nanoit.domain.payload.Report;
import kr.nanoit.module.broker.Broker;
import kr.nanoit.module.broker.BrokerImpl;
import kr.nanoit.module.inbound.socket.SocketManager;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


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
        messageService.insertMessageType("SEND");
        messageService.insertMessageType("SEND_ACK");
        messageService.insertMessageStatus("SENT", "RECEIVE");

        MemberEntity memberEntity = new MemberEntity(0, "이정섭", "$2a$12$9aqZtS4tclIN.sq3/J8qGuavmarzH5q5.z0Qz.7coXzD1MLjf0zRG", "test@test.com", new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()));
        messageService.insertUser(memberEntity);

        AgentEntity agentEntity = new AgentEntity(1, 1, 2, AgentStatus.DISCONNECTED, new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()));
        messageService.insertAgent(agentEntity);

        ClientMessageEntity clientMessageEntity = new ClientMessageEntity(0, 1, PayloadType.SEND, MessageStatus.RECEIVE, new Timestamp(System.currentTimeMillis()), "010-4444-5555", "064-444-5555", "이정섭", "테스트", new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()));
        messageService.insertClientMessage(clientMessageEntity);

        messageService.insertRelayCompany();
    }

    @BeforeEach
    void setUp() {
        this.uuid = UUID.randomUUID().toString();
        this.threadCarrier = spy(new ThreadCarrier(broker, uuid, messageService));
        this.carrierThread = spy(new Thread(threadCarrier));
        carrierThread.start();
    }

    @AfterEach
    void tearDown() {
        this.carrierThread.interrupt();
    }

    @DisplayName("Sender에서 보낸 데이터가 Carrier 모듈에서 정상적으로 데이터를 데이터베이스에 저장하고 Outbound 모듈로 전송 되어야 한다")
    @Test
    void t1() throws InterruptedException {
        // given
        ClientMessageEntity clientMessageEntity = new ClientMessageEntity(1, 1, PayloadType.SEND, MessageStatus.RECEIVE, new Timestamp(System.currentTimeMillis()), "010-4444-5555", "064-444-5555", "이정섭", "테스트", new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()));
        InternalDataCarrier expected = new InternalDataCarrier(new MetaData(uuid), new Payload(PayloadType.SEND_ACK, uuid, clientMessageEntity.toDto()));

        // when
        broker.publish(expected);

        // then
        Object object = broker.subscribe(InternalDataType.OUTBOUND);
        assertThat(object).isInstanceOf(InternalDataOutBound.class);
        InternalDataOutBound actual = (InternalDataOutBound) object;
        assertThat(actual.getMetaData().getSocketUuid()).isEqualTo(expected.getMetaData().getSocketUuid());
        assertThat(actual.getPayload().getType()).isEqualTo(PayloadType.REPORT);
        assertThat(actual.getPayload().getMessageUuid()).isEqualTo(expected.getPayload().getMessageUuid());
        assertThat(actual.getPayload().getData()).isInstanceOf(Report.class);

        Report report = (Report) actual.getPayload().getData();
        assertThat(report.getAgent_id()).usingRecursiveComparison().isEqualTo(clientMessageEntity.getAgent_id());
        assertThat(report.getResult()).isEqualTo(MessageResult.SUCCESS);
    }

    @DisplayName("ThreadCarrier에서 Exception이 발생 하면 에러 내용을 가진 Report가 전송 되어야 한다")
    @Test
    void t2() {
        // given , when , then
        doThrow(Exception.class).when(threadCarrier).run();

    }

    @DisplayName("ThreadCarrier에서 companyMessage를 데이터베이스에 잘못 된 값을 넣을 경우 에러 내용을 가진 Report가 전송 되어야 한다")
    @Test
    void t3() throws InterruptedException {
        // given
        ClientMessageEntity clientMessageEntity = new ClientMessageEntity(3, 1, PayloadType.SEND, MessageStatus.RECEIVE, new Timestamp(System.currentTimeMillis()), "010-4444-5555", "064-444-5555", "이정섭", "테스트", new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()));
        InternalDataCarrier expected = new InternalDataCarrier(new MetaData(uuid), new Payload(PayloadType.SEND_ACK, uuid, clientMessageEntity.toDto()));

        // when
        broker.publish(expected);

        // then
        Object object = broker.subscribe(InternalDataType.OUTBOUND);
        assertThat(object).isInstanceOf(InternalDataOutBound.class);
        InternalDataOutBound actual = (InternalDataOutBound) object;
        assertThat(actual.getMetaData().getSocketUuid()).isEqualTo(expected.getMetaData().getSocketUuid());
        assertThat(actual.getPayload().getType()).isEqualTo(PayloadType.REPORT);
        assertThat(actual.getPayload().getMessageUuid()).isEqualTo(expected.getPayload().getMessageUuid());
        assertThat(actual.getPayload().getData()).isInstanceOf(ErrorPayload.class);

        ErrorPayload errorPayload = (ErrorPayload) actual.getPayload().getData();
        assertThat(errorPayload.getReason()).isEqualTo("ERROR: insert or update on table \"company_message\" violates foreign key constraint \"company_message_client_message_id_fkey\"\n" +
                "  Detail: Key (client_message_id)=(3) is not present in table \"client_message\".");
    }

}