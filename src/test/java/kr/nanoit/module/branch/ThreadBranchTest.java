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

    @DisplayName("payloadType -> SEND : sender 로 변환 후 publish 해야 한다")
    @Test
    void t1() throws JsonProcessingException, InterruptedException {
        // given
        InternalDataBranch expected = new InternalDataBranch();
        expected.setMetaData(new MetaData(randomString(5)));
        expected.setPayload(new Payload(PayloadType.SEND, randomString(4), objectMapper.writeValueAsString(new Send(1, randomString(10), randomString(10), randomString(10)))));

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
    void t2() throws JsonProcessingException, InterruptedException {
        // given
        InternalDataBranch expected = new InternalDataBranch();
        expected.setMetaData(new MetaData(randomString(5)));
        expected.setPayload(new Payload(PayloadType.REPORT_ACK, randomString(4), objectMapper.writeValueAsString(new Send(1, randomString(10), randomString(10), randomString(10)))));

        // when
        broker.publish(expected);
        Thread.sleep(1000);
        Object actual = broker.subscribe(InternalDataType.OUTBOUND);

        // then
        assertThat(actual).isExactlyInstanceOf(InternalDataOutBound.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("payloadType -> ALIVE : outbound 로 변환 후 publish 해야 한다")
    @Test
    void t3() throws InterruptedException, JsonProcessingException {
        // given
        InternalDataBranch expected = new InternalDataBranch();
        expected.setMetaData(new MetaData(randomString(5)));
        expected.setPayload(new Payload(PayloadType.ALIVE, randomString(4), objectMapper.writeValueAsString(new Send(1, randomString(10), randomString(10), randomString(10)))));

        // when
        broker.publish(expected);
        Thread.sleep(1000);
        Object actual = broker.subscribe(InternalDataType.OUTBOUND);

        // then
        assertThat(actual).isExactlyInstanceOf(InternalDataOutBound.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @DisplayName("shoutDown 메소드가 실행되면 스레드는 종료")
    @Test
    void t4() throws InterruptedException {
        // given
        Thread.State actual = branchThread.getState();

        threadBranch.shoutDown();

        // when
        Thread.sleep(2000);
        Thread.State expected = branchThread.getState();

        // then
        assertThat(actual).isEqualTo(Thread.State.RUNNABLE);
        assertThat(expected).isEqualTo(Thread.State.TERMINATED);
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