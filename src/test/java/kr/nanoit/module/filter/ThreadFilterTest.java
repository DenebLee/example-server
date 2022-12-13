package kr.nanoit.module.filter;

import kr.nanoit.domain.broker.InternalDataFilter;
import kr.nanoit.domain.broker.InternalDataOutBound;
import kr.nanoit.domain.broker.InternalDataType;
import kr.nanoit.domain.payload.Authentication;
import kr.nanoit.domain.payload.ErrorDto;
import kr.nanoit.domain.payload.Payload;
import kr.nanoit.domain.payload.PayloadType;
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
        expected.setPayload(new Payload(PayloadType.ALIVE, "123123", new Authentication("test", "test")));

        // when
        broker.publish(expected);
        Thread.sleep(1000L);
        Object actual = broker.subscribe(InternalDataType.OUTBOUND);
        InternalDataOutBound actualAfter = (InternalDataOutBound) actual;

        // then
        assertThat(actual).isInstanceOf(InternalDataOutBound.class);
        assertThat(actualAfter.getPayload().getType()).isEqualTo(PayloadType.BAD_SEND);
        assertThat(actualAfter.getPayload().getData()).isInstanceOf(ErrorDto.class);
        assertThat(actualAfter.getPayload().getData()).isEqualTo("Data is null");


    }

    @DisplayName("validation : MetaData 가 null 일 경우 ErrorDto 가 포함된 InternalOutbound 가 생성되어야 함")
    @Test
    void t2() {

    }

    @DisplayName("validation : MetaData 가 null 일 경우 ErrorDto 가 포함된 InternalOutbound 가 생성되어야 함")
    @Test
    void t3() {

    }

    @DisplayName("validation : MetaData 가 null 일 경우 ErrorDto 가 포함된 InternalOutbound 가 생성되어야 함")
    @Test
    void t4() {

    }

    @DisplayName("validation : MetaData 가 null 일 경우 ErrorDto 가 포함된 InternalOutbound 가 생성되어야 함")
    @Test
    void t5() {

    }

    @DisplayName("validation : MetaData 가 null 일 경우 ErrorDto 가 포함된 InternalOutbound 가 생성되어야 함")
    @Test
    void t6() {

    }

    @DisplayName("validation : MetaData 가 null 일 경우 ErrorDto 가 포함된 InternalOutbound 가 생성되어야 함")
    @Test
    void t7() {

    }

    @DisplayName("validation : MetaData 가 null 일 경우 ErrorDto 가 포함된 InternalOutbound 가 생성되어야 함")
    @Test
    void t8() {

    }

    @DisplayName("validation : MetaData 가 null 일 경우 ErrorDto 가 포함된 InternalOutbound 가 생성되어야 함")
    @Test
    void t9() {

    }

    @DisplayName("validation : MetaData 가 null 일 경우 ErrorDto 가 포함된 InternalOutbound 가 생성되어야 함")
    @Test
    void t10() {

    }

}