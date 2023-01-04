package kr.nanoit.module.inbound.thread.gateway;

import kr.nanoit.module.broker.Broker;
import kr.nanoit.module.inbound.socket.SocketResource;
import kr.nanoit.module.inbound.socket.UserManager;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.Spy;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;


class ReadStreamThreadTest {
    @Mock
    private ServerSocket serverSocket;
    @Spy
    private ReadStreamThread readStreamThread;
    @Mock
    private Consumer<String> cleaner;
    @Mock
    private Thread thread;
    @Mock
    private Broker broker;
    @Mock
    private UserManager userManager;
    private AtomicBoolean resultAuth;
    private BufferedReader bufferedReader;
    @Spy
    private SocketResource socketResource;
    @Mock
    private Socket socket;


    @BeforeEach
    void setUp() {
        bufferedReader = mock(BufferedReader.class);
        String uuid = UUID.randomUUID().toString();
        readStreamThread = new ReadStreamThread(cleaner, broker, bufferedReader, uuid, resultAuth, userManager);
        thread = new Thread(readStreamThread);
        thread.start();
    }

    @AfterEach
    void tearDown() {
        thread.interrupt();
    }

    @DisplayName("인증 메시지를 5초동안 전달 받지 못하면  Exception이 발생 하여야 한다")
    @Test
    void t1() {
        // given

        // when
        assertThatThrownBy(() -> {
            when(bufferedReader.readLine()).thenReturn("123");
            Thread.sleep(5000);
        }).isInstanceOf(Exception.class).hasMessage("Authentication Timeout");
    }

    @DisplayName("클라이언트가 접속 하여 인증 메시지를 5초동안 보내지 않으면 TimeOut이 발생 해야 된다")
    @Test
    void t2() {
        // given


        // when


        // then

    }

}