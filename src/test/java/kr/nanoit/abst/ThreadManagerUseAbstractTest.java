package kr.nanoit.abst;

import kr.nanoit.module.branch.ThreadBranch;
import kr.nanoit.module.broker.Broker;
import kr.nanoit.module.broker.BrokerImpl;
import kr.nanoit.module.filter.ThreadFilter;
import kr.nanoit.module.inbound.socket.SocketManager;
import kr.nanoit.module.inbound.socket.ThreadTcpServer;
import kr.nanoit.module.mapper.ThreadMapper;
import kr.nanoit.module.outbound.ThreadOutBound;
import kr.nanoit.module.sender.ThreadSender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


class ThreadManagerUseAbstractTest {
    private Broker broker;
    private SocketManager socketManager;
    private ThreadManagerUseAbstract threadManagerUseAbstract;


    @BeforeEach
    void setUp() throws IOException {
        socketManager = new SocketManager();
        broker = new BrokerImpl(socketManager);
        threadManagerUseAbstract = ModuleProcess.threadManagerUseAbstract;
        new ThreadMapper(broker, getRandomUuid());
        new ThreadFilter(broker, getRandomUuid());
        new ThreadBranch(broker, getRandomUuid());
        new ThreadSender(broker, getRandomUuid());
        new ThreadOutBound(broker, getRandomUuid());
        new ThreadTcpServer(socketManager, broker, getRandomPort(), getRandomUuid());
    }

    @AfterEach
    void tearDown() {
        threadManagerUseAbstract.getObjectMap().clear();
        threadManagerUseAbstract.getThreadMap().clear();
        threadManagerUseAbstract.shutDown();
    }

    @DisplayName("ModuleProcess 를 상속받는 6개의 스레드가 objectHashMap 에 등록 되어야 함")
    @Test
    void should_six_object_registered_in_object_hashmap() {
        // given

        // when
        int expected = threadManagerUseAbstract.getObjectMapSize();

        // then
        assertThat(expected).isEqualTo(6);
    }

    @DisplayName("ModuleProcess 를 상속받는 6개의 스레드가 threadHashMap 에 등록 되어야 함")
    @Test
    void should_six_thread_registered_in_thread_hashmap() {
        // given

        // when
        int expected = threadManagerUseAbstract.getObjectMapSize();

        // then
        assertThat(expected).isEqualTo(6);
    }

    @DisplayName("중단된 스레드는 다시 실행되어야 한다")
    @Test
    void should_interrupted_thread_have_to_wait_1_second_before_running_again() throws InterruptedException {
        // given
        String uuid = null;
        for (Map.Entry<String, ModuleProcess> entry : threadManagerUseAbstract.getObjectMap().entrySet()) {
            if (entry.getValue() instanceof ThreadMapper) {
                uuid = entry.getKey();
            }
        }
        threadManagerUseAbstract.monitor();

        // when
        for (Map.Entry<String, Thread> entry : threadManagerUseAbstract.getThreadMap().entrySet()) {
            if (entry.getKey().equals(uuid)) {
                entry.getValue().interrupt();
                System.out.println("interrupt 건 스레드 조회 => " + entry.getValue().getName());
            }
        }

        // then
        Thread.sleep(2000);
        assertThat(threadManagerUseAbstract.getThreadMapSize()).isEqualTo(6);
    }

    @DisplayName("")
    @Test
    void should() throws IOException {
        // given


        // when


        // then

    }


    private String getRandomUuid() {
        return UUID.randomUUID().toString().substring(0, 7);
    }

    private int getRandomPort() {
        return new SecureRandom().nextInt(64511) + 1024;
    }

}