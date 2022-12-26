package kr.nanoit.module.filter;

import kr.nanoit.domain.broker.InternalDataFilter;
import kr.nanoit.domain.broker.InternalDataOutBound;
import kr.nanoit.domain.broker.InternalDataType;
import kr.nanoit.domain.broker.MetaData;
import kr.nanoit.domain.payload.*;
import kr.nanoit.domain.payload.ErrorPayload;
import kr.nanoit.module.broker.Broker;
import kr.nanoit.module.broker.BrokerImpl;
import kr.nanoit.module.inbound.socket.SocketManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

class ThreadFilterTest {

    @Mock
    SocketManager socketManager;

    private Thread filterThread;

    private ThreadFilter threadFilter;
    private Broker broker;
    private String uuid;

    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID().toString().substring(0, 7);
        broker = spy(new BrokerImpl(socketManager));
        threadFilter = spy(new ThreadFilter(broker, uuid));
        filterThread = new Thread(threadFilter);
        filterThread.start();
    }

    @AfterEach
    void tearDown() {
        filterThread.interrupt();
    }

    @DisplayName("validation : MetaData 가 null 일 경우 ErrorDto 가 포함된 InternalOutbound 가 생성되어야 함")
    @Test
    void t1() throws InterruptedException {
        // given
        InternalDataFilter expected = new InternalDataFilter();
        expected.setMetaData(null);
        expected.setPayload(new Payload(PayloadType.AUTHENTICATION, "123123", new Authentication("test", "test", "test")));

        // when
        broker.publish(expected);
        Thread.sleep(1000L);
        Object actual = broker.subscribe(InternalDataType.OUTBOUND);
        InternalDataOutBound actualAfter = (InternalDataOutBound) actual;
        Object object = actualAfter.getPayload().getData();
        ErrorPayload errorDto = (ErrorPayload) object;

        // then
        assertThat(actual).isInstanceOf(InternalDataOutBound.class);
        assertThat(actualAfter.getPayload().getType()).isEqualTo(PayloadType.BAD_SEND);
        assertThat(errorDto.getReason()).isEqualTo("MetaData is null");
    }

    @DisplayName("validation : MessageUuid 가 null 일 경우 ErrorDto 가 포함된 InternalOutbound 가 생성되어야 함")
    @Test
    void t2() throws InterruptedException {
        // given
        InternalDataFilter expected = new InternalDataFilter();
        expected.setMetaData(new MetaData(uuid));
        expected.setPayload(new Payload(PayloadType.AUTHENTICATION, null, new Authentication("test", "test", "test")));

        // when
        broker.publish(expected);
        Thread.sleep(1000L);
        Object actual = broker.subscribe(InternalDataType.OUTBOUND);
        InternalDataOutBound actualAfter = (InternalDataOutBound) actual;
        Object object = actualAfter.getPayload().getData();
        ErrorPayload errorDto = (ErrorPayload) object;

        // then
        assertThat(actual).isInstanceOf(InternalDataOutBound.class);
        assertThat(actualAfter.getPayload().getType()).isEqualTo(PayloadType.BAD_SEND);
        assertThat(errorDto.getReason()).isEqualTo("MessageUuid is null");
    }

    @DisplayName("validation : Payload.Type 이 null 일 경우 ErrorDto 가 포함된 InternalOutbound 가 생성되어야 함")
    @Test
    void t3() throws InterruptedException {
        InternalDataFilter expected = new InternalDataFilter();
        expected.setMetaData(new MetaData(uuid));
        expected.setPayload(new Payload(null, "123123", new Authentication("test", "test", "test")));

        // when
        broker.publish(expected);
        Thread.sleep(1000L);
        Object actual = broker.subscribe(InternalDataType.OUTBOUND);
        InternalDataOutBound actualAfter = (InternalDataOutBound) actual;
        Object object = actualAfter.getPayload().getData();
        ErrorPayload errorDto = (ErrorPayload) object;

        // then
        assertThat(actual).isInstanceOf(InternalDataOutBound.class);
        assertThat(actualAfter.getPayload().getType()).isEqualTo(PayloadType.BAD_SEND);
        assertThat(errorDto.getReason()).isEqualTo("Payload.Type is null");
    }

    @DisplayName("validation : Payload.Data 가 null 일 경우 ErrorDto 가 포함된 InternalOutbound 가 생성되어야 함")
    @Test
    void t4() throws InterruptedException {
        InternalDataFilter expected = new InternalDataFilter();
        expected.setMetaData(new MetaData(uuid));
        expected.setPayload(new Payload(PayloadType.AUTHENTICATION, "123123", null));

        // when
        broker.publish(expected);
        Thread.sleep(1000L);
        Object actual = broker.subscribe(InternalDataType.OUTBOUND);
        InternalDataOutBound actualAfter = (InternalDataOutBound) actual;
        Object object = actualAfter.getPayload().getData();
        ErrorPayload errorDto = (ErrorPayload) object;

        // then
        assertThat(actual).isInstanceOf(InternalDataOutBound.class);
        assertThat(actualAfter.getPayload().getType()).isEqualTo(PayloadType.BAD_SEND);
        assertThat(errorDto.getReason()).isEqualTo("Payload.Data is null");
    }

    @DisplayName("validation - PayloadData : Send 형식일 경우 PhoneNum 이 000 - 000 - 0000 형식이 아닐 경우 실패가 되어야 한다")
    @Test
    void t5() throws InterruptedException {
        // given
        InternalDataFilter expected = new InternalDataFilter();
        expected.setMetaData(new MetaData(uuid));
        expected.setPayload(new Payload(PayloadType.SEND, "123123", new Send(1, "1234-55-535", "054-745-4242", "테스트중")));

        // when
        broker.publish(expected);
        Thread.sleep(1000L);
        Object actual = broker.subscribe(InternalDataType.OUTBOUND);
        InternalDataOutBound actualAfter = (InternalDataOutBound) actual;
        Object object = actualAfter.getPayload().getData();
        ErrorPayload errorDto = (ErrorPayload) object;

        // then
        assertThat(actual).isInstanceOf(InternalDataOutBound.class);
        assertThat(actualAfter.getPayload().getType()).isEqualTo(PayloadType.BAD_SEND);
        assertThat(errorDto.getReason()).isEqualTo("Invalid Send value");
    }

    @DisplayName("validation - PayloadData : Send 형식일 경우 PhoneNum 에 공백이 있을 경우 실패가 되어야 한다")
    @Test
    void t6() throws InterruptedException {
        // given
        InternalDataFilter expected = new InternalDataFilter();
        expected.setMetaData(new MetaData(uuid));
        expected.setPayload(new Payload(PayloadType.SEND, "123123", new Send(1, " ", "054-745-4242", "테스트중")));

        // when
        broker.publish(expected);
        Thread.sleep(1000L);
        Object actual = broker.subscribe(InternalDataType.OUTBOUND);
        InternalDataOutBound actualAfter = (InternalDataOutBound) actual;
        Object object = actualAfter.getPayload().getData();
        ErrorPayload errorDto = (ErrorPayload) object;

        // then
        assertThat(actual).isInstanceOf(InternalDataOutBound.class);
        assertThat(actualAfter.getPayload().getType()).isEqualTo(PayloadType.BAD_SEND);
        assertThat(errorDto.getReason()).isEqualTo("Invalid Send value");
    }

    @DisplayName("validation - PayloadData : Send 형식일 경우 PhoneNum 에 큰 따옴표만  있을 경우 실패가 되어야 한다")
    @Test
    void t7() throws InterruptedException {
        // given
        InternalDataFilter expected = new InternalDataFilter();
        expected.setMetaData(new MetaData(uuid));
        expected.setPayload(new Payload(PayloadType.SEND, "123123", new Send(1, "", "054-745-4242", "테스트중")));

        // when
        broker.publish(expected);
        Thread.sleep(1000L);
        Object actual = broker.subscribe(InternalDataType.OUTBOUND);
        InternalDataOutBound actualAfter = (InternalDataOutBound) actual;
        Object object = actualAfter.getPayload().getData();
        ErrorPayload errorDto = (ErrorPayload) object;

        // then
        assertThat(actual).isInstanceOf(InternalDataOutBound.class);
        assertThat(actualAfter.getPayload().getType()).isEqualTo(PayloadType.BAD_SEND);
        assertThat(errorDto.getReason()).isEqualTo("Invalid Send value");
    }

    @DisplayName("validation - PayloadData : Send 형식일 경우 Callback 에 큰 따옴표만  있을 경우 실패가 되어야 한다")
    @Test
    void t8() throws InterruptedException {
        // given
        InternalDataFilter expected = new InternalDataFilter();
        expected.setMetaData(new MetaData(uuid));
        expected.setPayload(new Payload(PayloadType.SEND, "123123", new Send(1, "010-4444-5555", "", "테스트중")));

        // when
        broker.publish(expected);
        Thread.sleep(1000L);
        Object actual = broker.subscribe(InternalDataType.OUTBOUND);
        InternalDataOutBound actualAfter = (InternalDataOutBound) actual;
        Object object = actualAfter.getPayload().getData();
        ErrorPayload errorDto = (ErrorPayload) object;

        // then
        assertThat(actual).isInstanceOf(InternalDataOutBound.class);
        assertThat(actualAfter.getPayload().getType()).isEqualTo(PayloadType.BAD_SEND);
        assertThat(errorDto.getReason()).isEqualTo("Invalid Send value");
    }

    @DisplayName("validation - PayloadData : Send 형식일 경우 Callback 에 공백이 있을 경우 실패가 되어야 한다")
    @Test
    void t9() throws InterruptedException {
        // given
        InternalDataFilter expected = new InternalDataFilter();
        expected.setMetaData(new MetaData(uuid));
        expected.setPayload(new Payload(PayloadType.SEND, "123123", new Send(1, "010-4444-5555", " ", "테스트중")));

        // when
        broker.publish(expected);
        Thread.sleep(1000L);
        Object actual = broker.subscribe(InternalDataType.OUTBOUND);
        InternalDataOutBound actualAfter = (InternalDataOutBound) actual;
        Object object = actualAfter.getPayload().getData();
        ErrorPayload errorDto = (ErrorPayload) object;

        // then
        assertThat(actual).isInstanceOf(InternalDataOutBound.class);
        assertThat(actualAfter.getPayload().getType()).isEqualTo(PayloadType.BAD_SEND);
        assertThat(errorDto.getReason()).isEqualTo("Invalid Send value");
    }

    @DisplayName("validation - PayloadData : Send 형식일 경우 Callback 핸드폰 번호나 일반 전화번호 형식이 아닐 경우 실패가 되어야 한다")
    @Test
    void t10() throws InterruptedException {
        // given
        InternalDataFilter expected = new InternalDataFilter();
        expected.setMetaData(new MetaData(uuid));
        expected.setPayload(new Payload(PayloadType.SEND, "123123", new Send(1, "010-4444-5555", "01-5-646", "테스트중")));

        // when
        broker.publish(expected);
        Thread.sleep(1000L);
        Object actual = broker.subscribe(InternalDataType.OUTBOUND);
        InternalDataOutBound actualAfter = (InternalDataOutBound) actual;
        Object object = actualAfter.getPayload().getData();
        ErrorPayload errorDto = (ErrorPayload) object;

        // then
        assertThat(actual).isInstanceOf(InternalDataOutBound.class);
        assertThat(actualAfter.getPayload().getType()).isEqualTo(PayloadType.BAD_SEND);
        assertThat(errorDto.getReason()).isEqualTo("Invalid Send value");
    }

    @DisplayName("validation - PayloadData : Send 형식일 경우 Content 가 null 일 경우 실패가 되어야 한다")
    @Test
    void t11() throws InterruptedException {
        // given
        InternalDataFilter expected = new InternalDataFilter();
        expected.setMetaData(new MetaData(uuid));
        expected.setPayload(new Payload(PayloadType.SEND, "123123", new Send(1, "010-4444-5555", "054-948-5353", null)));

        // when
        broker.publish(expected);
        Thread.sleep(1000L);
        Object actual = broker.subscribe(InternalDataType.OUTBOUND);
        InternalDataOutBound actualAfter = (InternalDataOutBound) actual;
        Object object = actualAfter.getPayload().getData();
        ErrorPayload errorDto = (ErrorPayload) object;

        // then
        assertThat(actual).isInstanceOf(InternalDataOutBound.class);
        assertThat(actualAfter.getPayload().getType()).isEqualTo(PayloadType.BAD_SEND);
        assertThat(errorDto.getReason()).isEqualTo("Invalid Send value");
    }

    @DisplayName("validation - PayloadData : Send 형식일 경우 Content 에 큰 따옴표만 있을 경우 실패가 되어야 한다")
    @Test
    void t12() throws InterruptedException {
        // given
        InternalDataFilter expected = new InternalDataFilter();
        expected.setMetaData(new MetaData(uuid));
        expected.setPayload(new Payload(PayloadType.SEND, "123123", new Send(1, "010-4444-5555", "054-948-5353", "")));

        // when
        broker.publish(expected);
        Thread.sleep(1000L);
        Object actual = broker.subscribe(InternalDataType.OUTBOUND);
        InternalDataOutBound actualAfter = (InternalDataOutBound) actual;
        Object object = actualAfter.getPayload().getData();
        ErrorPayload errorDto = (ErrorPayload) object;

        // then
        assertThat(actual).isInstanceOf(InternalDataOutBound.class);
        assertThat(actualAfter.getPayload().getType()).isEqualTo(PayloadType.BAD_SEND);
        assertThat(errorDto.getReason()).isEqualTo("Invalid Send value");
    }

    @DisplayName("validation - PayloadData : Send 형식일 경우 Id가 0일 경우 실패가 되어야 한다")
    @Test
    void t13() throws InterruptedException {
        // given
        InternalDataFilter expected = new InternalDataFilter();
        expected.setMetaData(new MetaData(uuid));
        expected.setPayload(new Payload(PayloadType.SEND, "123123", new Send(0, "010-4444-5555", "054-948-5353", "테스트 중")));

        // when
        broker.publish(expected);
        Thread.sleep(1000L);
        Object actual = broker.subscribe(InternalDataType.OUTBOUND);
        InternalDataOutBound actualAfter = (InternalDataOutBound) actual;
        Object object = actualAfter.getPayload().getData();
        ErrorPayload errorDto = (ErrorPayload) object;

        // then
        assertThat(actual).isInstanceOf(InternalDataOutBound.class);
        assertThat(actualAfter.getPayload().getType()).isEqualTo(PayloadType.BAD_SEND);
        assertThat(errorDto.getReason()).isEqualTo("Invalid Send value");
    }

    @DisplayName("validation - PayloadData : Send 형식일 경우 Id가 0보다 작을 때 실패가 되어야 한다")
    @Test
    void t14() throws InterruptedException {
        // given
        InternalDataFilter expected = new InternalDataFilter();
        expected.setMetaData(new MetaData(uuid));
        expected.setPayload(new Payload(PayloadType.SEND, "123123", new Send(-11, "010-4444-5555", "054-948-5353", "")));

        // when
        broker.publish(expected);
        Thread.sleep(1000L);
        Object actual = broker.subscribe(InternalDataType.OUTBOUND);
        InternalDataOutBound actualAfter = (InternalDataOutBound) actual;
        Object object = actualAfter.getPayload().getData();
        ErrorPayload errorDto = (ErrorPayload) object;

        // then
        assertThat(actual).isInstanceOf(InternalDataOutBound.class);
        assertThat(actualAfter.getPayload().getType()).isEqualTo(PayloadType.BAD_SEND);
        assertThat(errorDto.getReason()).isEqualTo("Invalid Send value");
    }

    @DisplayName("validation - PayloadData : Alive 형식일 경우")
    @Test
    void t15() {

    }

    @DisplayName("validation - PayloadData : Report_ack 형식일 경우")
    @Test
    void t16() {

    }

    @DisplayName("shoutDown 메소드가 실행되면 스레드는 종료")
    @Test
    void t17() throws InterruptedException {
        // given
        Thread.State actual = filterThread.getState();

        threadFilter.shoutDown();

        // when
        Thread.sleep(2000);
        Thread.State expected = filterThread.getState();

        // then
        assertThat(actual).isEqualTo(Thread.State.RUNNABLE);
        assertThat(expected).isEqualTo(Thread.State.TERMINATED);
    }
}