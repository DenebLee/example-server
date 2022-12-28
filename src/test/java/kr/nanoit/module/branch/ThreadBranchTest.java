package kr.nanoit.module.branch;

import kr.nanoit.db.PostgreSqlDbcp;
import kr.nanoit.db.auth.MessageService;
import kr.nanoit.db.auth.MessageServiceImpl;
import kr.nanoit.domain.broker.*;
import kr.nanoit.domain.payload.Payload;
import kr.nanoit.domain.payload.PayloadType;
import kr.nanoit.domain.payload.Send;
import kr.nanoit.module.broker.Broker;
import kr.nanoit.module.broker.BrokerImpl;
import kr.nanoit.module.inbound.socket.SocketManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Timestamp;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

class ThreadBranchTest {

    private ThreadBranch threadBranch;
    @Mock
    private Broker broker;
    private Thread branchThread;
    @Mock
    SocketManager socketManager;
    @Mock
    private String uuid;
    private ObjectMapper objectMapper;
    private MessageService authService;
    private PostgreSqlDbcp dbcp;

    @BeforeEach
    void setUp() {
        dbcp = mock(PostgreSqlDbcp.class);
        broker = spy(new BrokerImpl(socketManager));
        objectMapper = new ObjectMapper();
        authService = mock(MessageServiceImpl.class);
        threadBranch = spy(new ThreadBranch(broker, uuid, authService));
        branchThread = new Thread(threadBranch);
        branchThread.start();
    }

    @AfterEach
    void tearDown() {
        branchThread.interrupt();
    }

    @DisplayName("payloadType -> SEND : Send_ACK 로 변환 후 publish 해야 한다")
    @Test
    void t1() throws JsonProcessingException, InterruptedException {
        // given
        InternalDataBranch expected = new InternalDataBranch();
        expected.setMetaData(new MetaData(randomString(5)));
        expected.setPayload(new Payload(PayloadType.SEND, randomString(4), objectMapper.writeValueAsString(new Send(1, new Timestamp(System.currentTimeMillis()), "010-4444-5555", "054-335-5353", "이정섭", "테스트"))));

        // when
        broker.publish(expected);
        Thread.sleep(1000);
        Object actual = broker.subscribe(InternalDataType.SENDER);

        // then
        assertThat(actual).isExactlyInstanceOf(InternalDataSender.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("payloadType -> REPORT_ACK : outbound 로 변환 후 publish 해야 한다")
    @Test
    void t2() {
        // given
        // when
        // then
    }

    @DisplayName("payloadType -> ALIVE : outbound 로 변환 후 publish 해야 한다")
    @Test
    void t3() {
        // given
        // when
        // then
    }

    public String randomString(int targetLength) {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetLength);
        for (int i = 0; i < targetLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }
}