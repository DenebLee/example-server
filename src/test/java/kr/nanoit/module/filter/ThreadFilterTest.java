package kr.nanoit.module.filter;

import kr.nanoit.domain.broker.InternalDataFilter;
import kr.nanoit.domain.broker.InternalDataOutBound;
import kr.nanoit.domain.broker.InternalDataType;
import kr.nanoit.domain.broker.MetaData;
import kr.nanoit.domain.payload.*;
import kr.nanoit.module.broker.Broker;
import kr.nanoit.module.broker.BrokerImpl;
import kr.nanoit.module.inbound.socket.SocketManager;
import kr.nanoit.module.inbound.socket.UserManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

class ThreadFilterTest {

    @Mock
    private SocketManager socketManager;

    private Thread filterThread;

    private ThreadFilter threadFilter;
    private Broker broker;
    private String uuid;
    @Mock
    private UserManager userManager;

    @BeforeEach
    void setUp() {
        uuid = UUID.randomUUID().toString();
        broker = spy(new BrokerImpl(socketManager));
        userManager = mock(UserManager.class);
        threadFilter = spy(new ThreadFilter(broker, uuid, userManager));
        filterThread = spy(new Thread(threadFilter));
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
        expected.setPayload(new Payload(PayloadType.AUTHENTICATION, "123123", new Authentication()));

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
        expected.setPayload(new Payload(PayloadType.AUTHENTICATION, null, new Authentication()));

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
        expected.setPayload(new Payload(null, "123123", new Authentication(1, "test", "test", "test")));

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

    @DisplayName("validation - PayloadData : Send 형식일 경우 Sender_num 이 000 - 000 - 0000 형식이거나 하이폰이 있는 경우 말고 다른 경우 실패가 되어야 한다")
    @Test
    void t5() throws InterruptedException {
        // given
        InternalDataFilter expected = new InternalDataFilter();
        expected.setMetaData(new MetaData(uuid));
        expected.setPayload(new Payload(PayloadType.SEND, "123123", new Send(1, "14-55-535", "054-745-4242", "이정섭", "테스트중")));

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

    @DisplayName("validation - PayloadData : Send 형식일 경우 Sender_num 에 공백이 있을 경우 실패가 되어야 한다")
    @Test
    void t6() throws InterruptedException {
        // given
        InternalDataFilter expected = new InternalDataFilter();
        expected.setMetaData(new MetaData(uuid));
        expected.setPayload(new Payload(PayloadType.SEND, "123123", new Send(1, " ", "054-745-4242", "이정섭", "테스트중")));

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

    @DisplayName("validation - PayloadData : Send 형식일 경우 Sender_num 에 큰 따옴표만  있을 경우 실패가 되어야 한다")
    @Test
    void t7() throws InterruptedException {
        // given
        InternalDataFilter expected = new InternalDataFilter();
        expected.setMetaData(new MetaData(uuid));
        expected.setPayload(new Payload(PayloadType.SEND, "123123", new Send(1, "", "054-745-4242", "이정섭", "테스트중")));

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

    @DisplayName("validation - PayloadData : Send 형식일 경우 Sender_callback 에 큰 따옴표만  있을 경우 실패가 되어야 한다")
    @Test
    void t8() throws InterruptedException {
        // given
        InternalDataFilter expected = new InternalDataFilter();
        expected.setMetaData(new MetaData(uuid));
        expected.setPayload(new Payload(PayloadType.SEND, "123123", new Send(1, "010-4444-5555", "", "이정섭", "x")));

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

    @DisplayName("validation - PayloadData : Send 형식일 경우 Sender_callback 에 공백이 있을 경우 실패가 되어야 한다")
    @Test
    void t9() throws InterruptedException {
        // given
        InternalDataFilter expected = new InternalDataFilter();
        expected.setMetaData(new MetaData(uuid));
        expected.setPayload(new Payload(PayloadType.SEND, "123123", new Send(1, "010-4444-5555", " ", "이정섭", "x")));

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

    @DisplayName("validation - PayloadData : Send 형식일 경우 Sender_callback 핸드폰 번호나 일반 전화번호 형식이 아닐 경우 실패가 되어야 한다")
    @Test
    void t10() throws InterruptedException {
        // given
        InternalDataFilter expected = new InternalDataFilter();
        expected.setMetaData(new MetaData(uuid));
        expected.setPayload(new Payload(PayloadType.SEND, "123123", new Send(1, "010-4444-5555", "", "이정섭", "x")));

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
        expected.setPayload(new Payload(PayloadType.SEND, "123123", new Send(1, "010-4444-5555", "054-745-4242", "이정섭", null)));

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
        expected.setPayload(new Payload(PayloadType.SEND, "123123", new Send(1, "010-4444-5555", "054-745-4242", "이정섭", "")));

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

    @DisplayName("validation - PayloadData : Send 형식일 경우 Agent_id가 0일 경우 실패가 되어야 한다")
    @Test
    void t13() throws InterruptedException {
        // given
        InternalDataFilter expected = new InternalDataFilter();
        expected.setMetaData(new MetaData(uuid));
        expected.setPayload(new Payload(PayloadType.SEND, "123123", new Send(1, "010-4444-5555", "054-745-4242", "이정섭", "안녕")));

        // when
        broker.publish(expected);
        Thread.sleep(1000L);

        // then
        Object object = broker.subscribe(InternalDataType.OUTBOUND);
        assertThat(object).isInstanceOf(InternalDataOutBound.class);
        InternalDataOutBound actual = (InternalDataOutBound) object;
        assertThat(actual.getMetaData().getSocketUuid()).isEqualTo(uuid);
        assertThat(actual.getPayload().getType()).isEqualTo(PayloadType.BAD_SEND);
        assertThat(actual.getPayload().getMessageUuid()).isEqualTo("123123");

        assertThat(actual.getPayload().getData()).isInstanceOf(ErrorPayload.class);
        ErrorPayload errorPayload = (ErrorPayload) actual.getPayload().getData();
        assertThat(errorPayload.getReason()).isEqualTo("Invalid Send value");


    }

    @DisplayName("validation - PayloadData : Send 형식일 경우 Sender_name이 null 일 경우 실패 되어야 한다")
    @Test
    void t14() throws InterruptedException {
        // given
        InternalDataFilter expected = new InternalDataFilter();
        expected.setMetaData(new MetaData(uuid));
        expected.setPayload(new Payload(PayloadType.SEND, "123123", new Send(1, "010-4444-5555", "054-745-4242", null, "안녕")));

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

    @DisplayName("validation - PayloadData : Send 형식일 경우 Sender_name에 큰따옴표만 있을 경우 실패 되어야 한다")
    @Test
    void t15() throws InterruptedException {
// given
        InternalDataFilter expected = new InternalDataFilter();
        expected.setMetaData(new MetaData(uuid));
        expected.setPayload(new Payload(PayloadType.SEND, "123123", new Send(1, "010-4444-5555", "054-745-4242", "", "안녕")));

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

    @DisplayName("validation - PayloadData : Send 형식일 경우 Sender_name에 공백이 있을 경우 실패 되어야 한다")
    @Test
    void t16() throws InterruptedException {
// given
        InternalDataFilter expected = new InternalDataFilter();
        expected.setMetaData(new MetaData(uuid));
        expected.setPayload(new Payload(PayloadType.SEND, "123123", new Send(1, "010-4444-5555", "054-745-4242", " ", "안녕")));

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

    @DisplayName("validation - PayloadData : Send 형식일 경우 Sender_name이 8글자를 넘어 갈 경우 실패 되어야 한다")
    @Test
    void t17() throws InterruptedException {
// given
        InternalDataFilter expected = new InternalDataFilter();
        expected.setMetaData(new MetaData(uuid));
        expected.setPayload(new Payload(PayloadType.SEND, "123123", new Send(1, "010-4444-5555", "054-745-4242", "8글자가넘어가면큰일납니다", "안녕")));

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

}