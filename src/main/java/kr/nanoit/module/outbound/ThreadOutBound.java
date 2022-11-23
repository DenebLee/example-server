package kr.nanoit.module.outbound;

import com.fasterxml.jackson.core.JsonProcessingException;
import kr.nanoit.abst.NanoItThread;
import kr.nanoit.domain.broker.InternalDataBranch;
import kr.nanoit.domain.broker.InternalDataOutBound;
import kr.nanoit.domain.broker.InternalDataSender;
import kr.nanoit.domain.broker.InternalDataType;
import kr.nanoit.domain.payload.PayloadType;
import kr.nanoit.module.auth.Auth;
import kr.nanoit.module.broker.Broker;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadOutBound extends NanoItThread {

    private final Auth auth;

    public ThreadOutBound(Broker broker, String uuid) {
        super(broker, uuid);
        this.auth = new Auth();
    }

    @Override
    public void execute() {
        try {
            this.flag = true;
            while (this.flag) {
                Object object = broker.subscribe(InternalDataType.OUTBOUND);
                if (object != null && object instanceof InternalDataOutBound) {
//                    log.info("[OUTBOUND] DATA INPUT => {}", object);
                    String payload = toJSON(object);
                    switch (((InternalDataOutBound) object).getPayload().getType()) {

                        // ReportACK? SEND_ACK, ALIVE_ACK, BAD_SEND,AUTHENTICATION_ACK

                        case SEND_ACK:
                            broker.outBound(((InternalDataOutBound) object).getMetaData().getSocketUuid(), payload);
//                            log.info("[OUTBOUND]   TO READ-THREAD => [{}]", payload);
                            break;
                        case ALIVE:
                            break;
                        case BAD_SEND:
                            break;
                        case AUTHENTICATION:
                            break;

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void shoutDown() {
        this.flag = false;
        log.warn("[OUTBOUND   THIS THREAD SHUTDOWN]");
    }

    @Override
    public Thread.State getState() {
        return this.thread.getState();
    }

    @Override
    public void sleep() throws InterruptedException {
        Thread.sleep(1000);
    }

    private String toJSON(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(((InternalDataOutBound) object).getPayload());
    }

}
