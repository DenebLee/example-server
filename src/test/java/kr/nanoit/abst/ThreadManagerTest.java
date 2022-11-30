package kr.nanoit.abst;

import kr.nanoit.module.branch.Branch;
import kr.nanoit.module.broker.Broker;
import kr.nanoit.module.broker.BrokerImpl;
import kr.nanoit.module.filter.Filter;
import kr.nanoit.module.inbound.TcpServer;
import kr.nanoit.module.inbound.socket.SocketManager;
import kr.nanoit.module.mapper.Mapper;
import kr.nanoit.module.outbound.OutBound;
import kr.nanoit.module.sender.Sender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;


class ThreadManagerTest {
    private Broker broker;
    private SocketManager socketManager;
    private ThreadManager threadManager;
    private int port;
    private Thread thread;


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
        threadManager.shoutDownThreadManager();
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
        for (Map.Entry<String, Process> entry : threadManager.originalObjects.entrySet()) {
            if (entry.getValue() instanceof Mapper) {
                uuid = entry.getKey();
            }
        }

        Thread.State actual = null;
        // when
        for (Map.Entry<String, Thread> entry : threadManager.currentThreads.entrySet()) {
            if (entry.getKey().equals(uuid)) {
                entry.getValue().interrupt();
                actual = entry.getValue().getState();
                System.out.println("interrupt 건 스레드 조회 => " + entry.getValue().getName());
            }

        }

        // then
        Thread.sleep(4000);
//        assertThat(actual).isEqualTo(Thread.State.TERMINATED);
        assertThat(threadManager.getCurrentThreadsSize()).isEqualTo(5);
    }


    private int getRandomPort() {
        return new SecureRandom().nextInt(64511) + 1024;
    }
}