package kr.nanoit.module.inbound;

import kr.nanoit.TestClient;
import kr.nanoit.domain.broker.InternalDataMapper;
import kr.nanoit.domain.broker.InternalDataType;
import kr.nanoit.module.broker.Broker;
import kr.nanoit.module.broker.BrokerImpl;
import kr.nanoit.module.inbound.socket.SocketManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class TcpServerTest {

    // 클라이언트를 일부러 붙였다가 때게
    // 서버 건들지말고

    //    private TestClient client;
    private Broker broker;
    private TcpServer tcpServer;
    private SocketManager socketManager;
    private final int port = 24242;

    public TcpServerTest() throws IOException {
        this.socketManager = new SocketManager();
//        this.client = new TestClient();
        this.broker = new BrokerImpl(socketManager);
        this.tcpServer = new TcpServer(socketManager, broker, port);
    }


    @BeforeEach
    void setUp() {
        tcpServer.serve();
    }

    @AfterEach
    void tearDown() throws IOException {
        tcpServer.connectClose();
        tcpServer.shutDown();
    }

    @Test
    @DisplayName("")
    // 클라이언트가 연결을 끊으면 정상적으로 리소스 정리 -> 해쉬맵 지우고 , 쓰레드 끄고 , 소켓 close 확인,
    void name() {
    }


    
    @Test
    @DisplayName("클라이언트가 요청 값을 broker에 publish 했을 떄 InternalDataMapper class로 맵핑된 데이터가 나와야 함")
    void should_return_internal_data_when_() throws InterruptedException, IOException {
        // given
        String expected = "{\"type\": \"SEND\",\"messageUuid\": \"test01\",\"data\": {\"id\": 123123, \"phone\": \"01044445555\", \"callback\": \"053555444\", \"content\": \" ㅎㅇㅎㅇㅎ\"}}";
        TestClient client = new TestClient();
        client.connect();
        client.write(expected);

        // when
        Object object = broker.subscribe(InternalDataType.MAPPER);
        InternalDataMapper actual = (InternalDataMapper) object;

        // then
        assertThat(object).isExactlyInstanceOf(InternalDataMapper.class);
        assertThat(actual.getPayload()).isEqualTo(expected);
    }

    @Test
    @DisplayName("")
    void check_socket_resource_manager_in_socket_map() {

    }


}