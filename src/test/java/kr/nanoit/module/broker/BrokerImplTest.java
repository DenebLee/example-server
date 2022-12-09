package kr.nanoit.module.broker;

import kr.nanoit.domain.payload.Authentication;
import kr.nanoit.domain.payload.Payload;
import kr.nanoit.domain.payload.PayloadType;
import kr.nanoit.module.inbound.socket.SocketManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

class BrokerImplTest {

    private ObjectMapper objectMapper;
    private String payload;
    private SocketManager socketManager;


    @BeforeEach
    void setUp() throws JsonProcessingException {
        socketManager = mock(SocketManager.class);
        objectMapper = new ObjectMapper();
        payload = objectMapper.writeValueAsString(new Payload(PayloadType.AUTHENTICATION, randomString(10), objectMapper.writeValueAsString(new Authentication(randomString(10), randomString(10)))));

    }

    @DisplayName("brokerQueue 에 InternalDataType 의 갯수만큼 값이 있어야 함")
    @Test
    void t1() {
        // given
        int internalDataTypeNumber = 6;

        // when
        Broker broker = spy(new BrokerImpl(socketManager));

        // then
        assertThat(broker.getSize()).isEqualTo(internalDataTypeNumber);
    }

    @DisplayName("")
    @Test
    void t2() {
        // given
        Broker broker = spy(new BrokerImpl(socketManager));

        // when

        // then
    }

    @DisplayName("")
    @Test
    void t3() {
        // given
        Broker broker = spy(new BrokerImpl(socketManager));

        // when

        // then
    }

    @DisplayName("")
    @Test
    void t4() {
        // given
        Broker broker = spy(new BrokerImpl(socketManager));

        // when

        // then
    }

    @DisplayName("")
    @Test
    void t5() {
        // given
        Broker broker = spy(new BrokerImpl(socketManager));

        // when

        // then
    }

    @DisplayName("")
    @Test
    void t6() {
        // given
        Broker broker = spy(new BrokerImpl(socketManager));

        // when

        // then
    }

    @DisplayName("")
    @Test
    void t7() {
        // given
        Broker broker = spy(new BrokerImpl(socketManager));

        // when

        // then
    }

    @DisplayName("")
    @Test
    void t8() {
        // given
        Broker broker = spy(new BrokerImpl(socketManager));

        // when

        // then
    }

    @DisplayName("")
    @Test
    void t9() {
        // given
        Broker broker = spy(new BrokerImpl(socketManager));

        // when

        // then
    }

    @DisplayName("")
    @Test
    void t10() {
        // given
        Broker broker = spy(new BrokerImpl(socketManager));

        // when

        // then
    }

    @DisplayName("")
    @Test
    void t11() {
        // given
        Broker broker = spy(new BrokerImpl(socketManager));

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