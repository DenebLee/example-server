package kr.nanoit.module.broker;

import kr.nanoit.domain.broker.*;
import kr.nanoit.domain.payload.Authentication;
import kr.nanoit.domain.payload.Payload;
import kr.nanoit.domain.payload.PayloadType;
import kr.nanoit.domain.payload.Send;
import kr.nanoit.module.inbound.socket.SocketManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class BrokerImplTestBefore {

    private Broker broker;
    private ObjectMapper objectMapper;
    private String payload;
    private SocketManager socketManager;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        socketManager = mock(SocketManager.class);
        broker = new BrokerImpl(socketManager);
        objectMapper = new ObjectMapper();
        payload = objectMapper.writeValueAsString(new Payload(PayloadType.AUTHENTICATION, randomString(10), objectMapper.writeValueAsString(new Authentication(randomString(10), randomString(10)))));
    }

    @AfterEach
    void tearDown() {
        broker = null;
    }

    @Test
    void should_get_mapper_data_when_input_mapper() throws InterruptedException {
        // given
        InternalDataMapper expected = new InternalDataMapper();
        expected.setMetaData(new MetaData(randomString(10)));
        expected.setPayload(payload);

        // when
        broker.publish(expected);
        Object actual = broker.subscribe(InternalDataType.MAPPER);

        // then
        assertThat(actual).isExactlyInstanceOf(InternalDataMapper.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void should_get_filter_data_when_input_filter() throws IOException, InterruptedException {
        // given
        InternalDataFilter expected = new InternalDataFilter();
        expected.setMetaData(new MetaData(randomString(10)));
        expected.setPayload(objectMapper.readValue(payload, Payload.class));
        System.out.println(expected.getPayload().toString());

        // when
        broker.publish(expected);
        Object actual = broker.subscribe(InternalDataType.FILTER);

        // then
        assertThat(actual).isExactlyInstanceOf(InternalDataFilter.class);
        // usingRecursiveComparison는 동등성을 보는것
        // 동등성은 DTO안에 있는 값이 일치하는지만 보고 동일성은 해당 DTO가 비교되는 DTO와 완벽 일치되는지 ex) 지폐라고 치면 같은 만원권은 만원이지만 일련번호까지 같으면 동일성이다
        // 필드값 비교를 통해 계산하면 nested한 객체도 함께 필드로 비교한다

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void should_get_branch_data_when_input_branch() throws IOException, InterruptedException {
        // given
        InternalDataBranch expected = new InternalDataBranch();
        expected.setMetaData(new MetaData(randomString(10)));
        expected.setPayload(objectMapper.readValue(payload, Payload.class));

        // when
        broker.publish(expected);
        Object actual = broker.subscribe(InternalDataType.BRANCH);

        //then
        assertThat(actual).isExactlyInstanceOf(InternalDataBranch.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void should_get_outbound_data_when_input_outbound() throws IOException, InterruptedException {
        // given
        InternalDataOutBound expected = new InternalDataOutBound();
        expected.setMetaData(new MetaData(randomString(11)));
        expected.setPayload(objectMapper.readValue(payload, Payload.class));

        // when
        broker.publish(expected);
        Object actual = broker.subscribe(InternalDataType.OUTBOUND);

        // then
        assertThat(actual).isExactlyInstanceOf(InternalDataOutBound.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void should_get_sender_data_when_input_sender() throws IOException, InterruptedException {
        // given
        InternalDataSender expected = new InternalDataSender();
        expected.setMetaData(new MetaData(randomString(4)));
        expected.setPayload(new Payload(PayloadType.SEND, randomString(4), objectMapper.writeValueAsString(new Send(1, randomString(10), randomString(10), randomString(10)))));

        // when
        broker.publish(expected);
        Object actual = broker.subscribe(InternalDataType.SENDER);

        // then
        assertThat(actual).isExactlyInstanceOf(InternalDataSender.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void should_get_auth_data_when_input_auth() {

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