package kr.nanoit.module.outbound;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.nanoit.abst.ModuleProcess;
import kr.nanoit.domain.broker.InternalDataOutBound;
import kr.nanoit.domain.broker.InternalDataType;
import kr.nanoit.domain.payload.Authentication;
import kr.nanoit.domain.payload.AuthenticationAck;
import kr.nanoit.domain.payload.ErrorPayload;
import kr.nanoit.domain.payload.Payload;
import kr.nanoit.module.broker.Broker;
import kr.nanoit.module.inbound.socket.UserManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class
ThreadOutBound extends ModuleProcess {

    private final ObjectMapper objectMapper;

    public ThreadOutBound(Broker broker, String uuid) {
        super(broker, uuid);
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void run() {
        try {
            this.flag = true;
            while (this.flag) {
                Object object = broker.subscribe(InternalDataType.OUTBOUND);
                if (object != null && object instanceof InternalDataOutBound) {
                    InternalDataOutBound internalDataOutBound = (InternalDataOutBound) object;
                    String payload = toJSON(internalDataOutBound);

                    if (broker.outBound(((InternalDataOutBound) object).getMetaData().getSocketUuid(), payload)) {
                    }
                }
            }
        } catch (InterruptedException | JsonProcessingException ex) {
            shoutDown();
            ex.printStackTrace();
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

    private String toJSON(InternalDataOutBound internalDataOutBound) throws JsonProcessingException {
        return objectMapper.writeValueAsString(internalDataOutBound.getPayload());
    }

    @Override
    public String getUuid() {
        return this.uuid;
    }

}
