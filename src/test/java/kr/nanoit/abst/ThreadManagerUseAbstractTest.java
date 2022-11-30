package kr.nanoit.abst;

import kr.nanoit.module.branch.ThreadBranch;
import kr.nanoit.module.broker.Broker;
import kr.nanoit.module.broker.BrokerImpl;
import kr.nanoit.module.filter.ThreadFilter;
import kr.nanoit.module.inbound.socket.SocketManager;
import kr.nanoit.module.mapper.Mapper;
import kr.nanoit.module.mapper.ThreadMapper;
import kr.nanoit.module.outbound.ThreadOutBound;
import kr.nanoit.module.sender.ThreadSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


class ThreadManagerUseAbstractTest {
    private Broker broker;
    private SocketManager socketManager;
    private ThreadManagerUseAbstract threadManagerUseAbstract;


    @BeforeEach
    void setUp() {
        socketManager = new SocketManager();
        broker = new BrokerImpl(socketManager);
        threadManagerUseAbstract = ModuleProcess.threadManagerUseAbstract;

    }

    @DisplayName("NanoitThread를 상속받는 5개의 스레드가 hashMap에 등록 되어야 함")
    @Test
    void should_five_thread_registered_in_hashmap() {
        // given
        new ThreadMapper(broker, getRandomUuid());
        new ThreadFilter(broker, getRandomUuid());
        new ThreadBranch(broker, getRandomUuid());
        new ThreadSender(broker, getRandomUuid());
        new ThreadOutBound(broker, getRandomUuid());


        // when
        int expected = threadManagerUseAbstract.getNanoItThreadMapSize();

        // then
        assertThat(expected).isEqualTo(5);
    }

    @DisplayName("중단된 스레드는 1초를 기다렸다가 다시 실행 되어야함")
    @Test
    void should_interrupted_thread_have_to_wait_1_second_before_running_again() throws InterruptedException {
        // given
        new ThreadMapper(broker, getRandomUuid());
        new ThreadBranch(broker, getRandomUuid());
        new ThreadFilter(broker, getRandomUuid());
        new ThreadSender(broker, getRandomUuid());
        new ThreadOutBound(broker, getRandomUuid());

        String uuid = null;
        for (Map.Entry<String, ModuleProcess> entry : threadManagerUseAbstract.objectMap.entrySet()) {
            if (entry.getValue() instanceof ThreadMapper) {
                uuid = entry.getKey();
            }
        }

        threadManagerUseAbstract.monitor();


        // when
        for (Map.Entry<String, Thread> entry : threadManagerUseAbstract.threadMap.entrySet()) {
            if (entry.getKey().equals(uuid)) {
                entry.getValue().interrupt();
                System.out.println("interrupt 건 스레드 조회 => " + entry.getValue().getName());
            }

        }

        // then
        Thread.sleep(4000);
        assertThat(threadManagerUseAbstract.getThreadMapSize()).isEqualTo(5);

        /*
        일반적으로 스레드가 run 되면 RUNNABLE 상태로 유지되며 TIME_WAITING 상태로 변경되는 경우는
        Blocking 걸렸거나(queue 같은) 혹은 THREAD.SLEEP 상태 일 경우
         */

    }

    @DisplayName("")
    @Test
    void should() {
        // given
        new ThreadMapper(broker, getRandomUuid());
        new ThreadBranch(broker, getRandomUuid());
        new ThreadFilter(broker, getRandomUuid());
        new ThreadSender(broker, getRandomUuid());
        new ThreadOutBound(broker, getRandomUuid());

        // when


        // then

    }


    private String getRandomUuid() {
        return UUID.randomUUID().toString();
    }
}