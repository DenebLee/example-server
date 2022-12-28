package kr.nanoit.module.sender;

import kr.nanoit.domain.broker.InternalDataCarrier;
import kr.nanoit.domain.broker.InternalDataSender;
import kr.nanoit.domain.broker.InternalDataType;
import kr.nanoit.domain.broker.MetaData;
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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.endsWith;
import static org.mockito.Mockito.*;

/*
 추후 DB 연결 까지 진행 할 예정
*/

class ThreadSenderTest {

    private ThreadSender threadSender;

    @Mock
    private SocketManager socketManager;
    private Thread senderThread;

    private Broker broker;
    private String uuid;

    @BeforeEach
    void setUp() {
        this.broker = spy(new BrokerImpl(socketManager));
        this.uuid = UUID.randomUUID().toString().substring(0, 7);
        this.threadSender = spy(new ThreadSender(broker, uuid));
        this.senderThread = spy(new Thread(threadSender));
//        this.senderThread.start();
    }

    @AfterEach
    void tearDown() {
        this.senderThread.interrupt();
    }

    @DisplayName("ThreadSender 는 InternalDataSender 를 받아 InternalDataCarrier 로 변환하여 Publish 해야 함")
    @Test
    void t1() throws InterruptedException {
        // given

        // when

        // then
    }

//    @DisplayName("")
//    @Test
//    void t2() throws InterruptedException {
//        // given , when , then
//        doThrow(InterruptedException.class).when(threadSender).run();
//        Thread.sleep(1000L);
//        verify(threadSender, atLeastOnce()).shoutDown();
//    }

//    @DisplayName("shoutDown 메소드가 실행되면 스레드는 종료 되어야 함")
//    @Test
//    void t3() throws InterruptedException {
//        // given
//        Thread.State actual = senderThread.getState();
//
//        threadSender.shoutDown();
//
//        // when
//        Thread.sleep(2000);
//        Thread.State expected = senderThread.getState();
//
//        // then
//        assertThat(actual).isEqualTo(Thread.State.RUNNABLE);
//        assertThat(expected).isEqualTo(Thread.State.TERMINATED);
//    }

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
//
//    @DisplayName("")
//    @Test
//    void t8() {
//
//    }
}