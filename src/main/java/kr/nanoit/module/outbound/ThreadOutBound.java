package kr.nanoit.module.outbound;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.nanoit.abst.ModuleProcess;
import kr.nanoit.domain.broker.InternalDataOutBound;
import kr.nanoit.domain.broker.InternalDataType;
import kr.nanoit.extension.Jackson;
import kr.nanoit.module.broker.Broker;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadOutBound extends ModuleProcess {

    private final ObjectMapper objectMapper;

    public ThreadOutBound(Broker broker, String uuid)  {
        super(broker, uuid);
        this.objectMapper = Jackson.getInstance().getObjectMapper();
    }

    @Override
    public void run() {
        try {
            this.flag = true;
            while (this.flag) {
                Object object = broker.subscribe(InternalDataType.OUTBOUND);
                if (object != null && object instanceof InternalDataOutBound) {
                    String payload = toJSON(object);

//                    log.info("[OUTBOUND] DATA INPUT => {}", object);
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
        } catch (InterruptedException | JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }

    }

    @Override
    public void shoutDown() {
        this.flag = false;
        log.warn("[OUTBOUND   THIS THREAD SHUTDOWN]");
    }


    @Override
    public void sleep() throws InterruptedException {
        Thread.sleep(1000);
    }

    private String toJSON(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(((InternalDataOutBound) object).getPayload());
    }

    @Override
    public String getUuid() {
        return this.uuid;
    }

}
