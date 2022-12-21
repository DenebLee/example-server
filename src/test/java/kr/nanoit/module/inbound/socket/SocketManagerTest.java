package kr.nanoit.module.inbound.socket;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

/*
     Test Double -> 스턴트 맨이라고 이해하면 편함.
     Mock -> 가짜를 의미한다. 실제와 동일한 기능을 하지는 않지만 대충 이런 기능이 이렇게 동작할 것이다 라고 알려주는 용도
     Stub -> 전체 중 일부 , 모든 기능 대신 일부 기능에 집중하여 임의로 구현한다
     Spy ->
*/

class SocketManagerTest {

    @DisplayName("register 호출: socket not bound")
    @Test
    void t1() {

        // given
        SocketManager socketManager = spy(new SocketManager());
        SocketResource socketResource = mock(SocketResource.class);
        Socket socket = mock(Socket.class);

        // when
        when(socketResource.getSocket()).thenReturn(socket);
        when(socket.isBound()).thenReturn(false);

        // then
        assertThat(socketManager.register(socketResource)).isFalse();
    }

    @DisplayName("register 호출: socket is closed")
    @Test
    void t2() {

        // given
        SocketManager socketManager = spy(new SocketManager());
        SocketResource socketResource = mock(SocketResource.class);
        Socket socket = mock(Socket.class);

        // when
        when(socketResource.getSocket()).thenReturn(socket);
        when(socket.isBound()).thenReturn(true);
        when(socket.isClosed()).thenReturn(true);

        // then
        assertThat(socketManager.register(socketResource)).isFalse();
    }

    @DisplayName("register 호출: socket is null")
    @Test
    void t3() {

        // given
        SocketManager socketManager = spy(new SocketManager());
        SocketResource socketResource = mock(SocketResource.class);

        // when
        when(socketResource.getSocket()).thenReturn(null);

        // then
        assertThat(socketManager.register(socketResource)).isFalse();
    }

    @DisplayName("register 호출: socketResource's uuid is null")
    @Test
    void t4() {

        // given
        SocketManager socketManager = spy(new SocketManager());
        SocketResource socketResource = mock(SocketResource.class);
        Socket socket = mock(Socket.class);

        // when
        when(socketResource.getSocket()).thenReturn(socket);
        when(socket.isBound()).thenReturn(true);
        when(socket.isClosed()).thenReturn(false);
        when(socketResource.getUuid()).thenReturn(null);

        // then
        assertThat(socketManager.register(socketResource)).isFalse();
    }

    @DisplayName("register 호출: socketResource 가 정상 등록 되어야 함")
    @Test
    void t5() {

        // given
        SocketManager socketManager = spy(new SocketManager());
        SocketResource socketResource = mock(SocketResource.class);
        Socket socket = mock(Socket.class);

        // when
        when(socketResource.getSocket()).thenReturn(socket);
        when(socket.isBound()).thenReturn(true);
        when(socket.isClosed()).thenReturn(false);
        when(socketResource.getUuid()).thenReturn(UUID.randomUUID().toString());

        // then
        assertThat(socketManager.register(socketResource)).isTrue();
    }

    @DisplayName("register 호출: 중복 체크")
    @Test
    void t6() {

        // given
        SocketManager socketManager = spy(new SocketManager());
        SocketResource socketResource = mock(SocketResource.class);
        Socket socket = mock(Socket.class);

        // when
        when(socketResource.getSocket()).thenReturn(socket);
        when(socket.isBound()).thenReturn(true);
        when(socket.isClosed()).thenReturn(false);
        when(socketResource.getUuid()).thenReturn(UUID.randomUUID().toString());

        // then
        assertThat(socketManager.register(socketResource)).isTrue();
        assertThat(socketManager.register(socketResource)).isFalse();
        assertThat(socketManager.getSocketResourcesMapSize()).isEqualTo(1);
    }

    @DisplayName("getSocketResource : uuid 를 통해 socketResource 성공적으로 가져와야 함")
    @Test
    void t7() {

        // given
        SocketManager socketManager = spy(new SocketManager());
        SocketResource socketResource = mock(SocketResource.class);
        Socket socket = mock(Socket.class);

        String uuid = UUID.randomUUID().toString();
        when(socketResource.getSocket()).thenReturn(socket);
        when(socket.isBound()).thenReturn(true);
        when(socket.isClosed()).thenReturn(false);
        when(socketResource.getUuid()).thenReturn(uuid);

        // when
        socketManager.register(socketResource);
        SocketResource expected = socketManager.getSocketResource(uuid);

        // then
        assertThat(expected).isEqualTo(socketResource);
        assertThat(socketManager.getSocketResourcesMapSize()).isEqualTo(1);
    }

    @DisplayName("getSocketResource : uuid 가 null 일 경우 null 을 반환 받아야 함")
    @Test
    void t8() {
        // given
        SocketManager socketManager = spy(new SocketManager());
        SocketResource socketResource = mock(SocketResource.class);
        Socket socket = mock(Socket.class);

        when(socketResource.getSocket()).thenReturn(socket);
        when(socket.isBound()).thenReturn(true);
        when(socket.isClosed()).thenReturn(false);
        when(socketResource.getUuid()).thenReturn(UUID.randomUUID().toString());

        // when
        socketManager.register(socketResource);
        SocketResource expected = socketManager.getSocketResource(null);

        // then
        assertThat(expected).isNull();

    }

    @DisplayName("getSocketResource : uuid 틀린 걸로 검색 했을때 없으면 null 이 떠야함")
    @Test
    void t9() {
        // given
        SocketManager socketManager = spy(new SocketManager());
        SocketResource socketResource = mock(SocketResource.class);
        Socket socket = mock(Socket.class);

        when(socketResource.getSocket()).thenReturn(socket);
        when(socket.isBound()).thenReturn(true);
        when(socket.isClosed()).thenReturn(false);
        when(socketResource.getUuid()).thenReturn(UUID.randomUUID().toString());

        // when
        socketManager.register(socketResource);
        SocketResource expected = socketManager.getSocketResource("12");

        // then
        assertThat(expected).isNull();
    }

    @DisplayName("SocketResource 가 Terminated, Input Output Stream 이 닫혔을 때 socket 연결이 끊겨야함")
    @Timeout(value = 2)
    @Test
    void t10() throws IOException {
        // given
        SocketManager socketManager = spy(new SocketManager());
        SocketResource socketResource = mock(SocketResource.class);
        Socket socket = mock(Socket.class);
        when(socketResource.getSocket()).thenReturn(socket);
        when(socket.isBound()).thenReturn(true);
        when(socket.isClosed()).thenReturn(false);
        when(socketResource.getUuid()).thenReturn(UUID.randomUUID().toString());
        socketManager.register(socketResource);

        // when
        when(socketResource.getSocket()).thenReturn(socket);
        when(socketResource.isTerminated()).thenReturn(true);
        when(socketResource.isSocketInputStreamClose()).thenReturn(true);
        when(socketResource.isSocketOutputStreamClose()).thenReturn(true);
        boolean expected = socketResource.getSocket().isConnected();

        // then
        assertThat(expected).isFalse();
    }

    @DisplayName("SocketResource 가 Terminated, Input Output Stream 이 닫히고 socket 연결이 끊겼을 때 HashMap 에 등록 하였던 SocketResource 가 삭제 되어야함 ")
    @Test
    void t11() throws InterruptedException {
        // given
        SocketManager socketManager = spy(new SocketManager());
        Thread testSocketManager = new Thread(socketManager);
        SocketResource socketResource = mock(SocketResource.class);
        Socket socket = mock(Socket.class);
        when(socketResource.getSocket()).thenReturn(socket);
        when(socket.isBound()).thenReturn(true);
        when(socket.isClosed()).thenReturn(false);
        when(socketResource.getUuid()).thenReturn(UUID.randomUUID().toString());
        socketManager.register(socketResource);
        int actual = socketManager.getSocketResourcesMapSize();

        // when
        when(socketResource.getSocket()).thenReturn(socket);
        when(socketResource.isTerminated()).thenReturn(true);
        when(socketResource.isSocketInputStreamClose()).thenReturn(true);
        when(socketResource.isSocketOutputStreamClose()).thenReturn(true);
        when(socketResource.getSocket().isClosed()).thenReturn(true);
        testSocketManager.start();
        Thread.sleep(2000);
        int expected = socketManager.getSocketResourcesMapSize();
        testSocketManager.interrupt();

        // then
        assertThat(actual).isEqualTo(1);
        assertThat(expected).isEqualTo(0);
    }
}