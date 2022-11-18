package kr.nanoit.module.inbound.socket;

import kr.nanoit.TestClient;
import kr.nanoit.module.broker.Broker;
import kr.nanoit.module.broker.BrokerImpl;
import kr.nanoit.module.inbound.TcpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("SocketManager 테스트 ")
class SocketManagerTest {

    private TcpServer tcpServer;
    private Broker broker;
    private final int port = 24242;
    private SocketManager socketManager;
    private TestClient client;
    private String payload;
    private Thread tcpServerThread;


    @BeforeEach
    void setUp() throws IOException {
        this.socketManager = new SocketManager();
        this.broker = new BrokerImpl(socketManager);
        this.tcpServer = new TcpServer(socketManager, broker, port);
        this.tcpServerThread = new Thread(this.tcpServer);
        this.payload = "{\"type\": \"SEND\",\"messageUuid\": \"test01\",\"data\": {\"id\": 123123, \"phone\": \"01044445555\", \"callback\": \"053555444\", \"content\": \" ㅎㅇㅎㅇㅎ\"}}";
        client = new TestClient();
        tcpServerThread.start();
    }

    @AfterEach
    void tearDown() {
        tcpServer.shutDown();
    }


    @Test
    @DisplayName("SocketManager -> Register")
    void should_register() throws InterruptedException {
        // given
        client.connect(port);
        client.delay();

        // when
        int socketManagerHashMapSize = socketManager.hashMapSize();

        // then
        assertThat(socketManagerHashMapSize).isEqualTo(1);
    }

    @Test
    @DisplayName("SocketManager -> 접속 끊겼을 때 hashmap사이즈가 0이여야 함")
    void should_hash_map_size_0_when_client_connect_close() throws InterruptedException, IOException {
        // givn
        client.connect(port);
        client.write(payload);
        int expected = socketManager.hashMapSize();
        System.out.println(expected);

        // when
        client.stop();
        int actual = socketManager.hashMapSize();
        System.out.println(actual);

        // then
        assertThat(expected).isEqualTo(1);
        assertThat(expected).isNotEqualTo(actual);

    }

    public String randomString(int targetLength) {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetLength);
        for (int i = 0; i < targetLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }
}