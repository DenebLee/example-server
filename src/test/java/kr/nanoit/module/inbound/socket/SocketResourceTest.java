package kr.nanoit.module.inbound.socket;

import kr.nanoit.module.broker.Broker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static org.mockito.Mockito.*;

class SocketResourceTest {
    @Mock
    private Socket socket;
    @Mock
    private SocketResource socketResource;

    @Mock
    private ServerSocket serverSocket;

    @Mock
    private Broker broker;

    @Mock
    private UserManager userManager;

    @BeforeEach
    void setUp() throws IOException {
        when(serverSocket.accept()).thenReturn(socket);
        when(socket.getOutputStream()).thenReturn(socket.getOutputStream());
        socketResource = spy(new SocketResource(socket, broker, userManager));
    }

    @DisplayName("SocketResource 객체가 생성되면 readStreamThread 와 writeStreamThread가 시작 되어야 한다")
    @Test
    void t1() throws IOException {
        // given, when
        // then
    }


}