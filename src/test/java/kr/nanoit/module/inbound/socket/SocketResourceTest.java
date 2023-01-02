package kr.nanoit.module.inbound.socket;

import kr.nanoit.module.broker.Broker;
import kr.nanoit.module.inbound.thread.gateway.ReadStreamThread;
import kr.nanoit.module.inbound.thread.gateway.WriteStreamThread;
import org.junit.Before;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.Socket;

import static org.mockito.Mockito.spy;

@RunWith(MockitoJUnitRunner.class)
class SocketResourceTest {

    @Mock
    private WriteStreamThread writeStreamThread;

    @Mock
    private Thread readThread;

    @Mock
    private Thread writeThread;

    @Mock
    private ReadStreamThread readStreamThread;
    @Mock
    private Broker broker;
    @Mock
    private Socket socket;
    @Mock
    private UserManager userManager;
    @Spy
    private SocketResource socketResource;

    @Before
    public void setUp() throws Exception {
        socketResource = spy(new SocketResource(socket, broker, userManager));
    }

    @DisplayName("serve : Read, Write 스레드가 interrupt 되었을 때 Cleaner 가 처리 해야함")
    @Test
    void t1() {
        // given


        // when


        // then
    }
}