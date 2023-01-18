package kr.nanoit.module.outbound;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.nanoit.db.auth.AuthenticaionStatus;
import kr.nanoit.domain.broker.InternalDataOutBound;
import kr.nanoit.domain.broker.MetaData;
import kr.nanoit.domain.payload.*;
import kr.nanoit.dto.UserInfo;
import kr.nanoit.module.broker.Broker;
import kr.nanoit.module.broker.BrokerImpl;
import kr.nanoit.module.inbound.socket.SocketManager;
import kr.nanoit.module.inbound.socket.UserManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Random;
import java.util.UUID;

import static org.mockito.Mockito.*;

class ThreadOutBoundTest {

    private ThreadOutBound threadOutBound;

    @Mock
    private SocketManager socketManager;


    private Broker broker;
    private String uuid;
    private Thread outBoundThread;
    private ObjectMapper objectMapper;
    private Random random;
    private UserManager userManager;


    @BeforeEach
    void setUp() {
        this.objectMapper = new ObjectMapper();
        this.random = new Random();
        this.userManager = mock(UserManager.class);
        this.uuid = UUID.randomUUID().toString().substring(0, 7);
        this.socketManager = mock(SocketManager.class);
        this.broker = spy(new BrokerImpl(this.socketManager));
        this.threadOutBound = spy(new ThreadOutBound(broker, uuid, socketManager, userManager));
        this.outBoundThread = spy(new Thread(threadOutBound));
        this.outBoundThread.start();
    }

    @AfterEach
    void tearDown() {
        this.outBoundThread.interrupt();
    }

    @DisplayName("ThreadOutbound는 전달 받은 데이터를 inbound WriteStream 으로 broker를 이용하여 전달 하여야 한다")
    @Test
    void t1() throws JsonProcessingException {

        // given
        Send send = new Send(1, "010-4987-5552", "056-555-6666", "이정섭", "테스트");
        InternalDataOutBound expected = new InternalDataOutBound(new MetaData(uuid), new Payload(PayloadType.SEND, uuid, send));

        // when
        broker.publish(expected);
        String payload = toJSON(expected);

        // then
        verify(broker).outBound(uuid, payload);
    }

    private String toJSON(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(((InternalDataOutBound) object).getPayload());
    }

}