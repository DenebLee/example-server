package kr.nanoit.module.branch;

import kr.nanoit.db.auth.MessageService;
import kr.nanoit.db.auth.MessageServiceImpl;
import kr.nanoit.domain.broker.InternalDataBranch;
import kr.nanoit.domain.broker.InternalDataSender;
import kr.nanoit.domain.broker.InternalDataType;
import kr.nanoit.domain.broker.MetaData;
import kr.nanoit.domain.payload.Payload;
import kr.nanoit.domain.payload.PayloadType;
import kr.nanoit.domain.payload.Send;
import kr.nanoit.module.broker.Broker;
import kr.nanoit.module.broker.BrokerImpl;
import kr.nanoit.module.inbound.socket.SocketManager;
import kr.nanoit.module.inbound.socket.UserManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

class ThreadBranchTest {

    private ThreadBranch threadBranch;
    @Mock
    private Broker broker;
    private Thread branchThread;
    @Mock
    SocketManager socketManager;
    @Mock
    private String uuid;
    @Mock
    private UserManager userManager;

    @Mock
    private MessageService messageService;


    @BeforeEach
    void setUp() {
        broker = spy(new BrokerImpl(socketManager));
        messageService = mock(MessageServiceImpl.class);
        threadBranch = spy(new ThreadBranch(broker, uuid, messageService, userManager));
        branchThread = spy(new Thread(threadBranch));
        branchThread.start();
    }

    @AfterEach
    void tearDown() {
        branchThread.interrupt();
    }

    @DisplayName("PayloadType이 SEND 일 경우 InternalDataSender로 변환후 sender로 전송 되어야 한다")
    @Test
    void t1() throws InterruptedException {
        // given
        Send send = new Send(2,"010-4081-1475", "053-676-5555", "이정섭", "테스트");
        InternalDataBranch expected = new InternalDataBranch(new MetaData(uuid), new Payload(PayloadType.SEND, uuid, send));

        // when
        broker.publish(expected);

        // then
        Object object = broker.subscribe(InternalDataType.SENDER);
        assertThat(object).isInstanceOf(InternalDataSender.class);
        InternalDataSender actual = (InternalDataSender) object;
        assertThat(actual.getMetaData().getSocketUuid()).isEqualTo(expected.getMetaData().getSocketUuid());
        assertThat(actual.getPayload().getType()).isEqualTo(PayloadType.SEND);
        assertThat(actual.getPayload().getMessageUuid()).isEqualTo(expected.getPayload().getMessageUuid());
        assertThat(actual.getPayload().getData()).isInstanceOf(Send.class);
        Send afterData = (Send) actual.getPayload().getData();
        assertThat(afterData.getPhoneNum()).isEqualTo(send.getPhoneNum());
        assertThat(afterData.getCallback()).isEqualTo(send.getCallback());
        assertThat(afterData.getName()).isEqualTo(send.getName());
        assertThat(afterData.getContent()).isEqualTo(send.getContent());
    }

}