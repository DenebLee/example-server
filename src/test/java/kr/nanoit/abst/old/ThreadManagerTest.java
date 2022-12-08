package kr.nanoit.abst.old;

import kr.nanoit.old.*;
import kr.nanoit.module.broker.Broker;
import kr.nanoit.module.broker.BrokerImpl;
import kr.nanoit.module.inbound.socket.SocketManager;
import kr.nanoit.old.Process;
import kr.nanoit.thread.ThreadManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


class ThreadManagerTest {
    private Broker broker;
    private SocketManager socketManager;
    private ThreadManager threadManager;
    private int port;


    @BeforeEach
    void setUp() throws IOException {
        port = getRandomPort();
        socketManager = new SocketManager();
        broker = new BrokerImpl(socketManager);
        threadManager = new ThreadManager(new Mapper(broker), new Filter(broker), new Branch(broker), new Sender(broker),
                new OutBound(broker), new TcpServer(socketManager, broker, port));
    }

    @AfterEach
    void tearDown() {
        threadManager.shutDownThreadManager();
    }

    @DisplayName("originalObject HashMap 에 6개의 값이 있어야 함(UUID, Thread)")
    @Test
    void should_hashmap_must_have_six_values() {
        // given
        threadManager.monitor();
        // when
        int expected = threadManager.getOriginalObjectSize();

        // then
        assertThat(expected).isEqualTo(6);

    }

    @DisplayName("currentThread HashMap 에 6개의 값이 있어야 함(UUID, Thread)")
    @Test
    void should_thread_hashmap_must_have_six_values() {
        // given
        threadManager.monitor();

        // when
        int expected = threadManager.getCurrentThreadsSize();

        // then
        assertThat(expected).isEqualTo(6);
    }

    @DisplayName("스레드 상태가 Terminated 일 경우 재시작 하여야 함")
    @Test
    void should_restart_when_thread_state_terminated() throws InterruptedException {
        // given
        threadManager.monitor();
        String uuid = null;
        for (Map.Entry<String, kr.nanoit.old.Process> entry : threadManager.originalObjects.entrySet()) {
            if (entry.getValue() instanceof Mapper) {
                uuid = entry.getKey();
            }
        }

        // when
        for (Map.Entry<String, Thread> entry : threadManager.currentThreads.entrySet()) {
            if (entry.getKey().equals(uuid)) {
                entry.getValue().interrupt();
                System.out.println("interrupt 건 스레드 조회 => " + entry.getValue().getName());
            }

        }

        // then
        Thread.sleep(2000);
        assertThat(threadManager.getStatusForTest).isTrue();
        assertThat(threadManager.getCurrentThreadsSize()).isEqualTo(6);
    }


    @DisplayName("시간 초과된 스레드는 재 시작 해야 된다 ")
    @Test
    void should_restart_when_thread_have_timed_out() {
        // given
        threadManager.monitor();

        // when
        String uuid = null;
        for (Map.Entry<String, Process> entry : threadManager.originalObjects.entrySet()) {
            if (entry.getValue() instanceof Branch) {
                uuid = entry.getKey();
            }
        }

        for (Map.Entry<String, Thread> entry : threadManager.currentThreads.entrySet()) {
            if (entry.getKey().equals(uuid)) {
                entry.getValue().interrupt();
                long time = threadManager.originalObjects.get(uuid).getRunningTime();
                System.out.println("총 스레드 실행 시간 측정 => " + time);
            }
        }

        // then
    }


    private int getRandomPort() {
        return new SecureRandom().nextInt(64511) + 1024;
    }
}