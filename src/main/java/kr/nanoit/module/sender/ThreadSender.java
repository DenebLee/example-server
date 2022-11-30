package kr.nanoit.module.sender;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.nanoit.abst.ModuleProcess;
import kr.nanoit.domain.broker.InternalDataOutBound;
import kr.nanoit.domain.broker.InternalDataSender;
import kr.nanoit.domain.broker.InternalDataType;
import kr.nanoit.domain.payload.Payload;
import kr.nanoit.domain.payload.PayloadType;
import kr.nanoit.domain.payload.Send;
import kr.nanoit.domain.payload.SendAck;
import kr.nanoit.extension.Jackson;
import kr.nanoit.module.broker.Broker;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadSender extends ModuleProcess {

    /**
     * sender는 통신사로 보내는 모듈
     * 실제로 통신사 소켓을 연결해서 관리해야 하지만 지금은 성공 또는 실패했다고 가정하고 코딩
     * 통신사 소켓 연결 후 지속적인 통신이 필요로 하기 때문에 Thread
     * * * - 성공 -> 아웃바운드로
     * * * - 실패 -> 아웃바운드로 ( 실패 메시지를 Client 로 전송 해야됨 )
     */
    private final ObjectMapper objectMapper;

    public ThreadSender(Broker broker, String uuid) {
        super(broker, uuid);
        this.objectMapper = Jackson.getInstance().getObjectMapper();
    }

    @Override
    public void run() {
        try {
            this.flag = true;
            while (this.flag) {
                Object object = broker.subscribe(InternalDataType.SENDER);

                if (object != null && object instanceof InternalDataSender) {
//                    log.info("[SENDER]   DATA INPUT => {}", object);
                    //TODO 가상으로 통신사로 보냈다고 가정한다.

                    Payload payload = ((InternalDataSender) object).getPayload();
                    Send send = objectMapper.convertValue(payload.getData(), Send.class);
                    SendAck sendAck = new SendAck(send.getId(), "SUCCESS");
                    Payload sendAckPayload = new Payload(PayloadType.SEND_ACK, payload.getMessageUuid(), sendAck);

                    if (broker.publish(new InternalDataOutBound(((InternalDataSender) object).getMetaData(), sendAckPayload))) {
//                        log.info("[SENDER]   TO OUTBOUND => [TYPE : {} DATA : {}]", ((InternalDataSender) object).getPayload().getType(), sendAckPayload);
                    }
                }
                // 실제 통신사와 socket통신 로직 추후 구현해야됨
                // Random boolean 으로 통신사 연결 여부 랜덤으로 제공
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void shoutDown() {
        this.flag = false;
        log.warn("[SENDER   THIS THREAD SHUTDOWN]");
    }

    @Override
    public void sleep() throws InterruptedException {
        Thread.sleep(1000);
    }
}
