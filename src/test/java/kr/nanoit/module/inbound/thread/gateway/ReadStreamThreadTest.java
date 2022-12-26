package kr.nanoit.module.inbound.thread.gateway;

import kr.nanoit.TestClient;
import kr.nanoit.module.broker.Broker;
import kr.nanoit.module.inbound.socket.SocketResource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Socket;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


class ReadStreamThreadTest {
    private SocketResource socketResource;
    private Socket socket;
    private final Broker broker;
    private final int port;
    private final TestClient client;

    ReadStreamThreadTest() {
        this.broker = mock(Broker.class);
        this.port = 55335;
        this.client = new TestClient();
    }


    @BeforeEach
    void setUp() throws IOException {
        this.socket = spy(new Socket());
        client.connect(this.port);
        this.socketResource = new SocketResource(this.socket, broker);
    }

    @AfterEach
    void tearDown() throws IOException {
        this.socket.close();
    }

    @DisplayName("")
    @Test
    void t1() throws IOException, InterruptedException {
        // given
        this.socketResource.serve();
        String data = "{\"type\": \"SEND\",\"messageUuid\": \"test01\",\"data\": {\"id\": 123123, \"phone\": \"01044445555\", \"callback\": \"053555444\", \"content\": \" 안녕하세요\"}}" + "\r\n";

        // when
        client.write(data, 1);
        // authentication 아닌 다른것 한번 전송

        // then
        socketResource.isTerminated();

    }

    @DisplayName("")
    @Test
    void t2() {

    }

    @DisplayName("")
    @Test
    void t3() {

    }

    @DisplayName("")
    @Test
    void t4() {

    }
}