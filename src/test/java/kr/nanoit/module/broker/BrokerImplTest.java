package kr.nanoit.module.broker;

import kr.nanoit.domain.broker.*;
import kr.nanoit.module.inbound.socket.SocketManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class BrokerImplTest {

    private SocketManager socketManager;

    @BeforeEach
    void setUp() {
        socketManager = mock(SocketManager.class);
    }

    @DisplayName("brokerQueue 에 InternalDataType 의 갯수만큼 값이 있어야 함")
    @Test
    void t1() {
        // given
        int internalDataTypeNumber = 6;

        // when
        Broker broker = spy(new BrokerImpl(socketManager));

        // then
        assertThat(broker.getBrokerMapSize()).isEqualTo(internalDataTypeNumber);
    }

    @DisplayName("publish :  internalDataType Mapper 를 offer 해야 한다 ")
    @Test
    void t2() throws InterruptedException {
        // given
        Broker broker = spy(new BrokerImpl(socketManager));
        InternalDataMapper internalDataMapper = mock(InternalDataMapper.class);
        broker.publish(internalDataMapper);

        // when
        int expected = broker.getInternalDataInBrokerMap(InternalDataType.MAPPER);

        // then
        assertThat(expected).isEqualTo(1);
        assertThat(broker.subscribe(InternalDataType.MAPPER)).isEqualTo(internalDataMapper);

    }

    @DisplayName("publish : internalDataType Filter 를 offer 해야 한다")
    @Test
    void t3() throws InterruptedException {
        // given
        Broker broker = spy(new BrokerImpl(socketManager));
        InternalDataFilter internalDataFilter = mock(InternalDataFilter.class);
        broker.publish(internalDataFilter);

        // when
        int expected = broker.getInternalDataInBrokerMap(InternalDataType.FILTER);

        // then
        assertThat(expected).isEqualTo(1);
        assertThat(broker.subscribe(InternalDataType.FILTER)).isEqualTo(internalDataFilter);
    }

    @DisplayName("publish : internalDataType Branch 를 offer 해야 한다")
    @Test
    void t4() throws InterruptedException {
        // given
        Broker broker = spy(new BrokerImpl(socketManager));
        InternalDataBranch internalDataBranch = mock(InternalDataBranch.class);
        broker.publish(internalDataBranch);

        // when
        int expected = broker.getInternalDataInBrokerMap(InternalDataType.BRANCH);

        // then
        assertThat(expected).isEqualTo(1);
        assertThat(broker.subscribe(InternalDataType.BRANCH)).isEqualTo(internalDataBranch);
    }

    @DisplayName("publish : internalDataType Sender 를 offer 해야 한다")
    @Test
    void t5() throws InterruptedException {
        // given
        Broker broker = spy(new BrokerImpl(socketManager));
        InternalDataSender internalDataSender = mock(InternalDataSender.class);
        broker.publish(internalDataSender);

        // when
        int expected = broker.getInternalDataInBrokerMap(InternalDataType.SENDER);

        // then
        assertThat(expected).isEqualTo(1);
        assertThat(broker.subscribe(InternalDataType.SENDER)).isEqualTo(internalDataSender);
    }

    @DisplayName("publish : internalDataType Outbound 를 offer 해야 한다")
    @Test
    void t6() throws InterruptedException {
        // given
        Broker broker = spy(new BrokerImpl(socketManager));
        InternalDataOutBound internalDataOutBound = mock(InternalDataOutBound.class);
        broker.publish(internalDataOutBound);

        // when
        int expected = broker.getInternalDataInBrokerMap(InternalDataType.OUTBOUND);

        // then
        assertThat(expected).isEqualTo(1);
        assertThat(broker.subscribe(InternalDataType.OUTBOUND)).isEqualTo(internalDataOutBound);
    }

    @DisplayName("publish : internalDataType Carrier 를 offer 해야 한다")
    @Test
    void t7() throws InterruptedException {
        // given
        Broker broker = spy(new BrokerImpl(socketManager));
        InternalDataCarrier internalDataCarrier = mock(InternalDataCarrier.class);
        broker.publish(internalDataCarrier);

        // when
        int expected = broker.getInternalDataInBrokerMap(InternalDataType.CARRIER);

        // then
        assertThat(expected).isEqualTo(1);
        assertThat(broker.subscribe(InternalDataType.CARRIER)).isEqualTo(internalDataCarrier);
    }

    @DisplayName("publish : object 가 InternalData Domain 랜덤 값에 instance of 할 경우 true 여야 함 ")
    @Test
    void t8() {
        // given
        Broker broker = spy(new BrokerImpl(socketManager));
        ArrayList list = new ArrayList(Arrays.asList(new InternalDataMapper(), new InternalDataFilter(), new InternalDataBranch(), new InternalDataSender(), new InternalDataOutBound()));
        System.out.println("선택 된 클래스는? -> " + list.get(new Random().nextInt(5)).getClass());
        Object object = mock((list.get(new Random().nextInt(5)).getClass()));

        // when
        boolean expected = broker.publish(object);

        // then
        assertThat(expected).isTrue();
    }

    @DisplayName("subscribe : InternalData class 중 하나를 subscribe 하면 poll 되어야 함")
    @Test
    void t9() throws InterruptedException {
        // given
        Broker broker = spy(new BrokerImpl(socketManager));
        InternalDataOutBound actual = mock(InternalDataOutBound.class);

        // when
        broker.publish(actual);
        Object expected = broker.subscribe(InternalDataType.OUTBOUND);

        // then
        assertThat(expected).isInstanceOf(InternalDataOutBound.class);
        assertThat(expected).isEqualTo(actual);
    }

    @DisplayName("outBound : parameter uuid 가 null 일경우 false 여야 함")
    @Test
    void t10() {
        // given
        Broker broker = spy(new BrokerImpl(socketManager));

        // when
        boolean expected = broker.outBound(null, "payloadTest");

        // then
        assertThat(expected).isFalse();
    }

    @DisplayName("outBound : parameter uuid 가 실질적인 값이 없을 경우 false 여야 함")
    @Test
    void t11() {
        // given
        Broker broker = spy(new BrokerImpl(socketManager));

        // when
        boolean expected = broker.outBound("", "payloadTest");

        // then
        assertThat(expected).isFalse();
    }

    @DisplayName("outBound : parameter uuid 에 공백이 있을 경우 false 여야 함")
    @Test
    void t12() {
        // given
        Broker broker = spy(new BrokerImpl(socketManager));

        // when
        boolean expected = broker.outBound(" uuid", "payloadTest");

        // then
        assertThat(expected).isFalse();
    }

    @DisplayName("outBound : parameter payload 에 실질적인 값이 없을 경우 false 여야 함")
    @Test
    void t13() {
        // given
        Broker broker = spy(new BrokerImpl(socketManager));

        // when
        boolean expected = broker.outBound(UUID.randomUUID().toString(), "");

        // then
        assertThat(expected).isFalse();
    }

    @DisplayName("outBound : parameter payload 이 null 일 경우 false 여야 함")
    @Test
    void t14() {
        // given
        Broker broker = spy(new BrokerImpl(socketManager));

        // when
        boolean expected = broker.outBound(UUID.randomUUID().toString(), null);

        // then
        assertThat(expected).isFalse();

    }

    @DisplayName("getInternalDataInBrokerMap : 임의의 InternalDataType 클래스를 넣은 횟수와 brokerQueue 의 사이즈가 같아야 함")
    @Test
    void t15() {
        // given
        Broker broker = spy(new BrokerImpl(socketManager));
        InternalDataSender internalDataSender = mock(InternalDataSender.class);

        // when
        int expected = 5;
        for (int i = 0; i < expected; i++) {
            broker.publish(internalDataSender);
        }

        // then
        assertThat(expected).isEqualTo(broker.getInternalDataInBrokerMap(InternalDataType.SENDER));
    }

}