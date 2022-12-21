package kr.nanoit.module.outbound;

import kr.nanoit.domain.broker.InternalDataOutBound;
import kr.nanoit.domain.broker.MetaData;
import kr.nanoit.domain.payload.Payload;
import kr.nanoit.domain.payload.PayloadType;
import kr.nanoit.domain.payload.Send;
import kr.nanoit.module.broker.Broker;
import kr.nanoit.module.broker.BrokerImpl;
import kr.nanoit.module.inbound.socket.SocketManager;
import kr.nanoit.module.inbound.socket.SocketResource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

class ThreadOutBoundTest {

    private ThreadOutBound threadOutBound;

    @Mock
    private SocketManager socketManager;

    private Broker broker;
    private String uuid;
    private Thread outBoundThread;

    @BeforeEach
    void setUp() {
        this.uuid = UUID.randomUUID().toString().substring(0, 7);
        this.broker = spy(new BrokerImpl(socketManager));
        this.threadOutBound = spy(new ThreadOutBound(broker, uuid));
        this.outBoundThread = spy(new Thread(threadOutBound));
        this.outBoundThread.start();
    }

    @AfterEach
    void tearDown() {
        this.outBoundThread.interrupt();
    }

    @DisplayName("ThreadOutBound 는 InternalDataOutBound 를 받아 broker.outbound 를 통해 전달해야 한다")
    @Test
    void t1() throws InterruptedException {
        // given
        InternalDataOutBound expected = new InternalDataOutBound();
        expected.setMetaData(new MetaData(uuid));
        expected.setPayload(new Payload(PayloadType.SEND_ACK, "123123", new Send(1, "010-4444-5555", "054-333-5555", "테스트 중")));

        // when
        broker.publish(expected);
        Thread.sleep(1000L);

        // then
        // Outbound Thread run 메소드 broker.subscribe 를 통해 outbound InternalData 를 가져온 다음 broker.outbound 를 통해 다시 socketResource
        // 에서 관리하는 writeThread 로 보냄

        System.out.println(broker.getOutBoundQueueSize(uuid));
    }

    @DisplayName("")
    @Test
    void t2() {
        // given
        InternalDataOutBound expected = new InternalDataOutBound();

        // when

        // then
    }

//    @DisplayName("")
//    @Test
//    void t3() {
//
//    }
//
//    @DisplayName("")
//    @Test
//    void t4() {
//
//    }
//
//    @DisplayName("")
//    @Test
//    void t5() {
//
//    }
//
//    @DisplayName("")
//    @Test
//    void t6() {
//
//    }
//
//    @DisplayName("")
//    @Test
//    void t7() {
//
//    }
}