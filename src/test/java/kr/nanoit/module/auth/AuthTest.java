package kr.nanoit.module.auth;

import kr.nanoit.db.DataBaseConfig;
import kr.nanoit.db.PostgreSqlDbcp;
import kr.nanoit.db.auth.MessageService;
import kr.nanoit.db.auth.MessageServiceImpl;
import kr.nanoit.domain.broker.InternalDataBranch;
import kr.nanoit.domain.broker.InternalDataOutBound;
import kr.nanoit.domain.broker.InternalDataType;
import kr.nanoit.domain.broker.MetaData;
import kr.nanoit.domain.entity.AgentEntity;
import kr.nanoit.domain.entity.MemberEntity;
import kr.nanoit.domain.payload.Authentication;
import kr.nanoit.domain.payload.ErrorPayload;
import kr.nanoit.domain.payload.Payload;
import kr.nanoit.domain.payload.PayloadType;
import kr.nanoit.module.broker.Broker;
import kr.nanoit.module.broker.BrokerImpl;
import kr.nanoit.module.inbound.socket.SocketManager;
import kr.nanoit.module.inbound.socket.UserManager;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;


@Testcontainers
class AuthTest {

    private static SocketManager socketManager;
    private static DataBaseConfig dataBaseConfig;
    private static PostgreSqlDbcp postgreSqlDbcp;
    private static MessageService messageService;
    private static UserManager userManager;
    private static Broker broker;
    private static Auth auth;
    private String uuid;


    @Container
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:14.5-alpine")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test");

    @BeforeAll
    static void beforeAll() throws ClassNotFoundException, URISyntaxException, IOException, SQLException {
        socketManager = mock(SocketManager.class);
        dataBaseConfig = new DataBaseConfig();
        dataBaseConfig.setIp(postgreSQLContainer.getHost())
                .setPort(postgreSQLContainer.getFirstMappedPort())
                .setDatabaseName(postgreSQLContainer.getDatabaseName())
                .setUsername(postgreSQLContainer.getUsername())
                .setPassword(postgreSQLContainer.getPassword());
        postgreSqlDbcp = new PostgreSqlDbcp(dataBaseConfig);
        messageService = new MessageServiceImpl(new PostgreSqlDbcp(dataBaseConfig));
        userManager = mock(UserManager.class);
        broker = spy(new BrokerImpl(socketManager));

        auth = new Auth(broker, userManager);
        postgreSqlDbcp.initSchema();

        // Table dependency data injection
        messageService.insertAccessList(2, "192.168.0.16");
        messageService.insertAgentStatus("CONNECTED", "DISCONNECTED");
        messageService.insertMessageType("AUTHENTICATION");
        messageService.insertMessageStatus("SENT", "RECEIVE");

        MemberEntity memberEntity = new MemberEntity(0, "이정섭", "$2a$12$9aqZtS4tclIN.sq3/J8qGuavmarzH5q5.z0Qz.7coXzD1MLjf0zRG", "test@test.com", new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()));
        messageService.insertUser(memberEntity);

        AgentEntity agentEntity = new AgentEntity(0, 1, 2, "DISCONNECTED", new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()));
        messageService.insertAgent(agentEntity);
        AgentEntity agentEntity2 = new AgentEntity(0, 1, 3, "DISCONNECTED", new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()));
        messageService.insertAgent(agentEntity);
    }

    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID().toString();
    }

    @DisplayName("인증 정보의 사용자가 데이터베이스에 없을때 실패 처리를 진행 해야 된다")
    @Test
    void t1() throws InterruptedException {
        // given
        Authentication expected = new Authentication(2, "이정", "이정섭", "test@test.com");

        // when
        auth.verificationAccount(makeFromBranchPayload(expected, uuid), messageService);

        // then
        Object object = broker.subscribe(InternalDataType.OUTBOUND);
        assertThat(object).isInstanceOf(InternalDataOutBound.class);
        InternalDataOutBound actual = (InternalDataOutBound) object;
        assertThat(actual.getMetaData().getSocketUuid()).isEqualTo(uuid);
        assertThat(actual.getPayload().getType()).isEqualTo(PayloadType.AUTHENTICATION_ACK);
        assertThat(actual.getPayload().getMessageUuid()).isEqualTo(uuid);
        assertThat(actual.getPayload().getData()).isInstanceOf(ErrorPayload.class);
        ErrorPayload errorPayload = (ErrorPayload) actual.getPayload().getData();
        assertThat(errorPayload.getReason()).isEqualTo("Authentication failure Account information verification required");
    }


    @DisplayName("인증 정보의 사용자의 비밀번호가 틀렸을 때 실패 처리가 진행 되어야 한다")
    @Test
    void t2() throws InterruptedException {
        // given
        Authentication expected = new Authentication(2, "이정섭", "리정섭동무", "test@test.com");

        // when
        auth.verificationAccount(makeFromBranchPayload(expected, uuid), messageService);

        // then
        Object object = broker.subscribe(InternalDataType.OUTBOUND);
        assertThat(object).isInstanceOf(InternalDataOutBound.class);
        InternalDataOutBound actual = (InternalDataOutBound) object;
        assertThat(actual.getMetaData().getSocketUuid()).isEqualTo(uuid);
        assertThat(actual.getPayload().getType()).isEqualTo(PayloadType.AUTHENTICATION_ACK);
        assertThat(actual.getPayload().getMessageUuid()).isEqualTo(uuid);
        assertThat(actual.getPayload().getData()).isInstanceOf(ErrorPayload.class);
        ErrorPayload errorPayload = (ErrorPayload) actual.getPayload().getData();
        assertThat(errorPayload.getReason()).isEqualTo("Failed to Authentication");
    }

    @DisplayName("인증 정보의 사용자가 요청한 Agent 정보가 데이터베이스에 없으면 실패 처리가 진행 되어야 한다")
    @Test
    void t3() throws InterruptedException {
        // given
        Authentication expected = new Authentication(1, "이정섭", "이정섭", "test@test.com");

        // when
        auth.verificationAccount(makeFromBranchPayload(expected, uuid), messageService);

        // then
        Object object = broker.subscribe(InternalDataType.OUTBOUND);
        assertThat(object).isInstanceOf(InternalDataOutBound.class);
        InternalDataOutBound actual = (InternalDataOutBound) object;
        assertThat(actual.getMetaData().getSocketUuid()).isEqualTo(uuid);
        assertThat(actual.getPayload().getType()).isEqualTo(PayloadType.AUTHENTICATION_ACK);
        assertThat(actual.getPayload().getMessageUuid()).isEqualTo(uuid);
        assertThat(actual.getPayload().getData()).isInstanceOf(ErrorPayload.class);
        ErrorPayload errorPayload = (ErrorPayload) actual.getPayload().getData();
        assertThat(errorPayload.getReason()).isEqualTo("Agent does not exist");
    }

    @DisplayName("인증 정보의 사용자가 요청한 Agent 가 허용된 접속 리스트에 없을 경우 실패 처리가 진행 되어야 한다")
    @Test
    void t4() throws InterruptedException {
        // given
        Authentication expected = new Authentication(2, "이정섭", "이정섭", "test@test.com");

        // when
        auth.verificationAccount(makeFromBranchPayload(expected, uuid), messageService);

        // then
        Object object = broker.subscribe(InternalDataType.OUTBOUND);
        assertThat(object).isInstanceOf(InternalDataOutBound.class);
        InternalDataOutBound actual = (InternalDataOutBound) object;
        assertThat(actual.getMetaData().getSocketUuid()).isEqualTo(uuid);
        assertThat(actual.getPayload().getType()).isEqualTo(PayloadType.AUTHENTICATION_ACK);
        assertThat(actual.getPayload().getMessageUuid()).isEqualTo(uuid);
        assertThat(actual.getPayload().getData()).isInstanceOf(ErrorPayload.class);
        ErrorPayload errorPayload = (ErrorPayload) actual.getPayload().getData();
        assertThat(errorPayload.getReason()).isEqualTo("Requested agent is not allowed agent");
    }

    @NotNull
    private static InternalDataBranch makeFromBranchPayload(Authentication data, String uuid) {
        return new InternalDataBranch(new MetaData(uuid), new Payload(PayloadType.AUTHENTICATION, uuid, data));
    }
}