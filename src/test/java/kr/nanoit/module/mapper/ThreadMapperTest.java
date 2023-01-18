package kr.nanoit.module.mapper;

import kr.nanoit.domain.broker.InternalDataFilter;
import kr.nanoit.domain.broker.InternalDataMapper;
import kr.nanoit.domain.broker.InternalDataType;
import kr.nanoit.domain.broker.MetaData;
import kr.nanoit.domain.payload.Authentication;
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
        uuid = UUID.randomUUID().toString();
        this.objectMapper = new ObjectMapper();
        threadMapper = spy(new ThreadMapper(broker, uuid));
        mapperThread = new Thread(threadMapper);
        mapperThread.start();
    }

    @AfterEach
    void tearDown() {
        mapperThread.interrupt();
    }

    @DisplayName("Payload : Authenticaion 메시지를 String 값으로 넘겼을 때 Mapping 되어 InternalDataFilter 안에 data는 Authentication 형태로 Filter로 전송되어야 함")
    @Test
    void t1() throws InterruptedException, JsonProcessingException {
        // given
        String uuid = UUID.randomUUID().toString();
        Authentication authentication = new Authentication(1, "이정섭", "이정섭", "test@test.com");
        Payload expected = new Payload(PayloadType.AUTHENTICATION, "1", authentication);
        String sendData = objectMapper.writeValueAsString(expected);

        InternalDataMapper actual = new InternalDataMapper(new MetaData(uuid), sendData);

        // when
        broker.publish(actual);
        Thread.sleep(1000L);


        // then
        Object object = broker.subscribe(InternalDataType.FILTER);
        assertThat(object).isInstanceOf(InternalDataFilter.class);
        InternalDataFilter internalDataFilter = (InternalDataFilter) object;
        assertThat(internalDataFilter.getMetaData()).isEqualTo(actual.getMetaData());
        assertThat(internalDataFilter.getPayload().getType()).isEqualTo(PayloadType.AUTHENTICATION);
        assertThat(internalDataFilter.getPayload().getMessageUuid()).isEqualTo(expected.getMessageUuid());
        assertThat(internalDataFilter.getPayload().getData()).isInstanceOf(Authentication.class);
        Authentication dataAcutal = (Authentication) internalDataFilter.getPayload().getData();
        assertThat(dataAcutal).usingRecursiveComparison().isEqualTo(authentication);

    }

    @DisplayName("Payload : Send 메시지를 String 값으로 넘겼을 때 Mapping 되어 InternalDataFilter 안에 data는 Send 형태로 Filter로 전송되어야 함")
    @Test
    void t2() throws InterruptedException, JsonProcessingException {
        // given
        String uuid = UUID.randomUUID().toString();
        Send send = new Send(1, "010-4444-5555", "053-444-555", "이정섭", "테스트");
        Payload expected = new Payload(PayloadType.SEND, "1", send);
        String sendData = objectMapper.writeValueAsString(expected);

        InternalDataMapper actual = new InternalDataMapper(new MetaData(uuid), sendData);

        // when
        broker.publish(actual);
        Thread.sleep(1000L);


        // then
        Object object = broker.subscribe(InternalDataType.FILTER);
        assertThat(object).isInstanceOf(InternalDataFilter.class);
        InternalDataFilter internalDataFilter = (InternalDataFilter) object;
        assertThat(internalDataFilter.getMetaData()).isEqualTo(actual.getMetaData());
        assertThat(internalDataFilter.getPayload().getType()).isEqualTo(PayloadType.SEND);
        assertThat(internalDataFilter.getPayload().getMessageUuid()).isEqualTo(expected.getMessageUuid());
        assertThat(internalDataFilter.getPayload().getData()).isInstanceOf(Send.class);
        Send dataAcutal = (Send) internalDataFilter.getPayload().getData();
        assertThat(dataAcutal).usingRecursiveComparison().isEqualTo(send);

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