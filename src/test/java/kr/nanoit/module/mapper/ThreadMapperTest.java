package kr.nanoit.module.mapper;

import kr.nanoit.domain.broker.InternalDataFilter;
import kr.nanoit.domain.broker.InternalDataMapper;
import kr.nanoit.domain.broker.InternalDataType;
import kr.nanoit.domain.broker.MetaData;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;


class ThreadMapperTest {
    @Mock
    private SocketManager socketManager;
    private ThreadMapper threadMapper;
    private String uuid;
    private Broker broker;
    private Thread mapperThread;
    private ObjectMapper objectMapper;


    @BeforeEach
    void setUp() {
        broker = spy(new BrokerImpl(socketManager));
        uuid = UUID.randomUUID().toString().substring(0, 7);
        this.objectMapper = new ObjectMapper();
        threadMapper = spy(new ThreadMapper(broker, uuid));
        mapperThread = new Thread(threadMapper);
        mapperThread.start();
    }

    @AfterEach
    void tearDown() {
        mapperThread.interrupt();
    }

    @DisplayName("Payload : String 값으로 넘겼을 때 Mapping 되어 InternalDataFilter 가 되어야 함 ")
    @Test
    void t1() throws InterruptedException, JsonProcessingException {
        // given
        String data = "{\"type\":\"SEND\",\"messageUuid\":\"test01\",\"data\":{\"id\":123123,\"phone\":\"01044445555\",\"callback\":\"053555444\",\"content\":\" 안녕하세요\"}}";
        InternalDataMapper actual = new InternalDataMapper(new MetaData(randomString(5)), data);

        // when
        broker.publish(actual);
        Thread.sleep(1000L);
        Object object = broker.subscribe(InternalDataType.FILTER);
        InternalDataFilter expected = (InternalDataFilter) object;

        // then
        assertThat(actual.getPayload()).isEqualTo(objectMapper.writeValueAsString(expected.getPayload()));
    }

    @DisplayName("shoutDown 메소드가 실행되면 스레드는 종료 되어야 함")
    @Test
    void t2() throws InterruptedException {
        // given
        Thread.State actual = mapperThread.getState();

        threadMapper.shoutDown();

        // when
        Thread.sleep(1500L);
        Thread.State expected = mapperThread.getState();

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