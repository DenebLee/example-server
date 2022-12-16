package kr.nanoit.abst;

import kr.nanoit.module.broker.Broker;
import kr.nanoit.module.broker.BrokerImpl;
import kr.nanoit.module.inbound.socket.SocketManager;
import kr.nanoit.module.mapper.ThreadMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;


class ModuleProcessManagerImplTest {
    private Broker broker;
    private SocketManager socketManager;
    private ModuleProcessManagerImpl moduleProcessManagerImpl;


    @BeforeEach
    void setUp() {
        socketManager = spy(SocketManager.class);
        broker = spy(new BrokerImpl(socketManager));
        moduleProcessManagerImpl = spy(ModuleProcessManagerImpl.class);
    }


    @DisplayName("register null 넣을때 register 실패 되야 함")
    @Timeout(value = 2)
    @Test
    void t1() {
        // given, when ,then
        assertThat(moduleProcessManagerImpl.register(null)).isFalse();
    }

    @DisplayName("register 했을때 objectMap 사이즈가 1이 되어야함")
    @Test
    void t2() {
        // given
        ModuleProcess moduleProcess = spy(new ThreadMapper(broker, "123"));

        // when
        moduleProcessManagerImpl.register(moduleProcess);

        // then
        assertThat(moduleProcessManagerImpl.getObjectMapSize()).isEqualTo(1);
    }

    @DisplayName("register null 넣을 때 register 실패 되어야 함")
    @Test
    void t3() {
        // given
        ModuleProcess moduleProcess = spy(new ThreadMapper(broker, null));

        // when, then
        assertThat(moduleProcessManagerImpl.register(null)).isFalse();
    }

    @DisplayName("register 에 중복 된 값을 넣으면 실패 되어야 함")
    @Timeout(value = 2)
    @Test
    void t4() {
        // given
        String uuid = getRandomUuid();
        ModuleProcess moduleProcess1 = spy(new ThreadMapper(broker, uuid));
        ModuleProcess moduleProcess2 = spy(new ThreadMapper(broker, uuid));

        // when
        boolean expected1 = moduleProcessManagerImpl.register(moduleProcess1);
        boolean expected2 = moduleProcessManagerImpl.register(moduleProcess2);

        // then
        assertThat(expected1).isTrue();
        assertThat(expected2).isFalse();
    }

    @DisplayName("register 정상 입력")
    @Timeout(value = 2)
    @Test
    void t5() {
        // given
        ModuleProcess moduleProcess = spy(new ThreadMapper(broker, getRandomUuid()));

        // when
        boolean expected = moduleProcessManagerImpl.register(moduleProcess);

        // then
        assertThat(expected).isTrue();
    }

    @DisplayName("register 3개가 동시에 전달 될 때 정상 등록되어야 함")
    @Timeout(value = 2)
    @Test
    void t6() {
        // given
        ModuleProcess moduleProcess1 = spy(new ThreadMapper(broker, getRandomUuid()));
        ModuleProcess moduleProcess2 = spy(new ThreadMapper(broker, getRandomUuid()));
        ModuleProcess moduleProcess3 = spy(new ThreadMapper(broker, getRandomUuid()));

        // when
        boolean expected = moduleProcessManagerImpl.register(moduleProcess1, moduleProcess2, moduleProcess3);

        // then
        assertThat(expected).isTrue();
    }


    @DisplayName("unregister 했을때 정상 삭제가 되어야 함")
    @Timeout(value = 2)
    @Test
    void t7() {
        // given
        String uuid = getRandomUuid();
        ModuleProcess moduleProcess = spy(new ThreadMapper(broker, uuid));
        moduleProcessManagerImpl.register(moduleProcess);

        // when
        boolean expected = moduleProcessManagerImpl.unregister(uuid);

        // then
        assertThat(expected).isTrue();
    }

    @DisplayName("unregister 했을때 전달하는 uuid 가 null 일 경우 실패해야 됨")
    @Timeout(value = 2)
    @Test
    void t8() {
        // given
        ModuleProcess moduleProcess = spy(new ThreadMapper(broker, getRandomUuid()));
        moduleProcessManagerImpl.register(moduleProcess);

        // when
        boolean expected = moduleProcessManagerImpl.unregister(null);

        // then
        assertThat(expected).isFalse();
    }

    @DisplayName("등록이 되어있을때 등록된 횟수와 ObjectMap 의 사이즈가 같아야 함")
    @Timeout(value = 2)
    @Test
    void t9() {
        // given , when
        int totalRegisterCount = 3;
        for (int i = 0; i < totalRegisterCount; i++) {
            ModuleProcess moduleProcess = spy(new ThreadMapper(broker, getRandomUuid()));
            moduleProcessManagerImpl.register(moduleProcess);
        }

        // then
        assertThat(totalRegisterCount).isEqualTo(moduleProcessManagerImpl.getObjectMapSize());

    }

    @DisplayName("등록 시킨 ModuleProcess 갯수와 ThreadMap 의 사이즈가 같아야 함")
    @Timeout(value = 2)
    @Test
    void t10() {
        // given, when
        int totalRegisterCount = 3;
        for (int i = 0; i < totalRegisterCount; i++) {
            ModuleProcess moduleProcess = spy(new ThreadMapper(broker, getRandomUuid()));
            moduleProcessManagerImpl.register(moduleProcess);
        }

        // then
        assertThat(totalRegisterCount).isEqualTo(moduleProcessManagerImpl.getThreadMapSize());
    }

    @DisplayName("unregister 한 횟수만큼 objectMap 사이즈도 감소 해야됨")
    @Timeout(value = 2)
    @Test
    void t11() {
        // given
        int attemptedRegisterCount = 0;
        int attemptedUnregisterCount = 0;

        ArrayList<String> uuidList = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            ModuleProcess moduleProcess = spy(new ThreadMapper(broker, getRandomUuid()));
            moduleProcessManagerImpl.register(moduleProcess);
            uuidList.add(moduleProcess.getUuid());
            attemptedRegisterCount++;
        }
        int objectMapSize = moduleProcessManagerImpl.getObjectMapSize();
        int threadMapSize = moduleProcessManagerImpl.getThreadMapSize();

        // when
        for (int i = 0; i < 54; i++) {
            moduleProcessManagerImpl.unregister(uuidList.get(i));
            attemptedUnregisterCount++;
        }
        int expected = moduleProcessManagerImpl.getObjectMapSize();


        int totalCount = attemptedRegisterCount - attemptedUnregisterCount;

        // then
        assertThat(objectMapSize).isEqualTo(attemptedRegisterCount);
        assertThat(threadMapSize).isEqualTo(attemptedRegisterCount);
        assertThat(expected).isEqualTo(totalCount);
    }

    @DisplayName("runningThread 의 갯수가 objectMap 과 threadMap 의 사이즈와 같아야 함")
    @Timeout(value = 2)
    @Test
    void t12() throws InterruptedException {
        // given
        ModuleProcess moduleProcess = spy(new ThreadMapper(broker, getRandomUuid()));

        // when
        moduleProcessManagerImpl.register(moduleProcess);
        Thread.sleep(1000L);
        long expected = moduleProcessManagerImpl.runningThreadCount();

        // when
        assertThat(moduleProcessManagerImpl.getObjectMapSize()).isEqualTo(expected);
        assertThat(moduleProcessManagerImpl.getThreadMapSize()).isEqualTo(expected);
    }

    @DisplayName("objectMap 에서 unregister 했을때 uuid 가 같은 threadMap 의 사이즈도 감소해야됨")
    @Timeout(value = 2)
    @Test
    void t13() throws InterruptedException {
        // given
        ModuleProcess moduleProcess = spy(new ThreadMapper(broker, getRandomUuid()));
        moduleProcessManagerImpl.register(moduleProcess);

        // when
        moduleProcessManagerImpl.unregister(moduleProcess.getUuid());
        Thread.sleep(1400L); // -> 1.4초로 하니 통과 1.5 이상은 TImeOutException뜸
        int count = moduleProcessManagerImpl.getObjectMapSize();
        int expected = moduleProcessManagerImpl.getThreadMapSize();

        /// then
        assertThat(count).isEqualTo(0);
        assertThat(expected).isEqualTo(0);
    }

    @DisplayName("interruptThread 에 전달할 uuid 가 null 이면 false 여야 함")
    @Timeout(value = 2)
    @Test
    void t14() {
        // given , when , then
        assertThat(moduleProcessManagerImpl.interruptThread(null)).isFalse();
    }

    @DisplayName("interruptThread 실행시 threadMap 의 사이즈가 줄어야 함")
    @Timeout(value = 2)
    @Test
    void t15() throws InterruptedException {
        // given
        ModuleProcess moduleProcess = spy(new ThreadMapper(broker, getRandomUuid()));
        moduleProcessManagerImpl.register(moduleProcess);
        int actual = moduleProcessManagerImpl.getThreadMapSize();

        // when
        moduleProcessManagerImpl.interruptThread(moduleProcess.getUuid());
        moduleProcessManagerImpl.unregister(moduleProcess.getUuid());
        Thread.sleep(1500L);
        int expected = moduleProcessManagerImpl.getThreadMapSize();

        // then
        assertThat(actual).isEqualTo(1);
        assertThat(expected).isEqualTo(0);
    }


    private String getRandomUuid() {
        return UUID.randomUUID().toString().substring(0, 7);
    }

    private int getRandomPort() {
        return new SecureRandom().nextInt(64511) + 1024;
    }
}