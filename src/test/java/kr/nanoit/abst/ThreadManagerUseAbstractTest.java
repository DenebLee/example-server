package kr.nanoit.abst;

import kr.nanoit.module.broker.Broker;
import kr.nanoit.module.broker.BrokerImpl;
import kr.nanoit.module.inbound.socket.SocketManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;


class ThreadManagerUseAbstractTest {
    private Broker broker;
    private SocketManager socketManager;
    private ThreadManagerUseAbstract threadManagerUseAbstract;


    @BeforeEach
    void setUp() {
        socketManager = spy(SocketManager.class);
        broker = new BrokerImpl(socketManager);
        threadManagerUseAbstract = ModuleProcess.threadManagerUseAbstract;
    }

    @AfterEach
    void tearDown() {

    }

    @DisplayName("register 메소드에 대한 성공 여부 테스트")
    @Test
    void t1() {
        // given
        ModuleProcess moduleProcess = spy(ModuleProcess.class);

        // when
        threadManagerUseAbstract.register(moduleProcess);

        // then
        assertThat(threadManagerUseAbstract.getObjectMapSize()).isEqualTo(1);
    }

    @DisplayName("register null 넣을 때 register 실패 되어야 함")
    @Test
    void t2() {
        // given
        ModuleProcess moduleProcess = spy(ModuleProcess.class);

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