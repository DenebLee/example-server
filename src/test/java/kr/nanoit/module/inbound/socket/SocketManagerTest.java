package kr.nanoit.module.inbound.socket;

import kr.nanoit.TestClient;
import kr.nanoit.module.broker.Broker;
import kr.nanoit.module.broker.BrokerImpl;
import kr.nanoit.old.TcpServer;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("SocketManager 테스트 ")
class SocketManagerTest {

    private TcpServer tcpServer;
    private Broker broker;
    private int port;
    private SocketManager socketManager;
    private TestClient client;
    private String payload;
    private Thread tcpServerThread;
    private Thread socketManagerThread;


    @BeforeEach
    void setUp() throws IOException {
        port = getRandomPort();
        this.socketManager = new SocketManager();
        this.broker = new BrokerImpl(socketManager);
        this.tcpServer = new TcpServer(socketManager, broker, port);
        this.tcpServerThread = new Thread(this.tcpServer);
        this.socketManagerThread = new Thread(this.socketManager);
        this.payload = "{\"type\": \"SEND\",\"messageUuid\": \"test01\",\"data\": {\"id\": 123123, \"phone\": \"01044445555\", \"callback\": \"053555444\", \"content\": \" ㅎㅇㅎㅇㅎ\"}}";
        client = new TestClient();
        socketManagerThread.start();
        tcpServerThread.start();
    }

    @AfterEach
    void tearDown() throws InterruptedException {
        socketManager.socketManagerStop();
        tcpServer.shutDown();
        Thread.sleep(1000);
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
    @DisplayName("SocketManager -> 접속 끊겼을 때 hashmap 사이즈가 0이여야 함")
    void should_hash_map_size_0_when_client_connect_close() throws InterruptedException, IOException {
        // given
        client.connect(port);
        client.write(payload, 1);
        int expected = socketManager.hashMapSize();

        // when
        client.stop();
        Thread.sleep(1000);

        // then
        assertThat(expected).isEqualTo(1);
        assertThat(socketManager.hashMapSize()).isEqualTo(0);
    }

    @Test
    @DisplayName("SocketManager -> 접속 끊겼을 때 각 stream의 자원이 정리 되어야 함")
    void should_resources_of_each_stream_must_be_organized() throws IOException, InterruptedException {
        // given
        client.connect(port);
        client.write(payload, 3);

        // when
        client.stop();
        Thread.sleep(1000);
        boolean expected = socketManager.isTestResult();


        // then
        assertThat(expected).isTrue();
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

    private int getRandomPort() {
        return new SecureRandom().nextInt(64511) + 1024;
    }

}