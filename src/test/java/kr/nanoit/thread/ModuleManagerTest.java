package kr.nanoit.thread;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class ModuleManagerTest {

    @Disabled
    @DisplayName("register null 넣을때 register 실패 되야 함")
    @Timeout(value = 2)
    @Test
    void t1() {
        // given
        ModuleManager moduleManager = ModuleManager.moduleManager();
        // when, then
        assertThat(moduleManager.register(null)).isFalse();
    }

    @DisplayName("register uuid 없는거 넣을때 register 실패 되야함")
    @Timeout(value = 2)
    @Test
    void t2() {
        // given
        ModuleManager moduleManager = new ModuleManagerImpl();

        // when
        Module module = spy(Module.class);
        when(module.getUuid()).thenReturn(null);

        // when, then
        assertThat(moduleManager.register(module)).isFalse();
    }

    @DisplayName("register uuid 중복된거 넣을때 register 실패 되야함")
    @Timeout(value = 2)
    @Test
    void t3() {
        // given
        ModuleManager moduleManager = new ModuleManagerImpl();
        String uuid = UUID.randomUUID().toString();
        Module module1 = spy(Module.class);
        Module module2 = spy(Module.class);

        // when
        when(module1.getUuid()).thenReturn(uuid);
        when(module2.getUuid()).thenReturn(uuid);

        // then
        assertThat(moduleManager.register(module1)).isTrue();
        assertThat(moduleManager.register(module2)).isFalse();
    }

    @DisplayName("register 정상 입력")
    @Timeout(value = 2)
    @Test
    void t4() {
        // given
        ModuleManager moduleManager = new ModuleManagerImpl();
        Module module1 = spy(Module.class);

        // when
        when(module1.getUuid()).thenReturn(UUID.randomUUID().toString());
        moduleManager.register(module1);

        // then
        assertThat(moduleManager.moduleTotal()).isEqualTo(1);
    }

    @DisplayName("register 3개가 동시에 입력될때 정상 등록되야 함")
    @Timeout(value = 2)
    @Test
    void t5() {
        // given
        ModuleManager moduleManager = new ModuleManagerImpl();
        Module module1 = spy(Module.class);
        Module module2 = spy(Module.class);
        Module module3 = spy(Module.class);

        // when
        when(module1.getUuid()).thenReturn(UUID.randomUUID().toString());
        when(module2.getUuid()).thenReturn(UUID.randomUUID().toString());
        when(module3.getUuid()).thenReturn(UUID.randomUUID().toString());
        boolean expected = moduleManager.register(module1, module2, module3);

        // then
        assertThat(expected).isTrue();
        assertThat(moduleManager.moduleTotal()).isEqualTo(3);
    }

//    @DisplayName("register 3개가 동시에 입력될때 uuid가 중복되면 3개 모두 등록 실패 되야 됨")
//    @Timeout(value = 2)
//    @Test
//    void t6() {
//        // given
//        ModuleManager moduleManager = new ModuleManagerImpl();
//
//        // when
//        String uuid = UUID.randomUUID().toString();
//        moduleManager.register(new TestModule(uuid));
//
//        // then
//        assertThat(moduleManager.register(new TestModule(UUID.randomUUID().toString()), new TestModule(UUID.randomUUID().toString()), new TestModule(uuid))).isFalse();
//        assertThat(moduleManager.moduleTotal()).isEqualTo(1);
//    }
//
//    @DisplayName("register 3개가 동시에 입력될때 한개가 uuid가 null 이면 모두 등록 실패 되야 됨")
//    @Timeout(value = 2)
//    @Test
//    void t7() {
//        // given
//        ModuleManager moduleManager = new ModuleManagerImpl();
//
//        // when, then
//        assertThat(moduleManager.register(new TestModule(UUID.randomUUID().toString()), new TestModule(UUID.randomUUID().toString()), new TestModule(null))).isFalse();
//        assertThat(moduleManager.moduleTotal()).isEqualTo(0);
//    }
}