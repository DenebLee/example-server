package kr.nanoit.module.sender;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.nanoit.abst.ModuleProcess;
import kr.nanoit.domain.broker.*;
import kr.nanoit.domain.payload.Payload;
import kr.nanoit.domain.payload.PayloadType;
import kr.nanoit.domain.payload.Send;
import kr.nanoit.domain.payload.SendAck;
import kr.nanoit.extension.Jackson;
import kr.nanoit.module.broker.Broker;
import lombok.extern.slf4j.Slf4j;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

@Slf4j
public class ThreadSender extends ModuleProcess {

    public ThreadSender(Broker broker, String uuid) {
        super(broker, uuid);
    }

    @Override
    public void run() {
        try {
            this.flag = true;
            while (this.flag) {
                Object object = broker.subscribe(InternalDataType.SENDER);
                if (object != null && object instanceof InternalDataSender) {
//                    log.info("[SENDER]   DATA INPUT => {}", object);
                    InternalDataSender internalDataSender = (InternalDataSender) object;
                    Payload payload = ((InternalDataSender) object).getPayload();
                    if (internalDataSender != null && payload != null) {
                        if (payload.getType().equals(PayloadType.SEND)) {
                            if (broker.publish(new InternalDataCarrier(internalDataSender.getMetaData(), payload))) ;
                        }
                    }
                }
            }
        } catch (InterruptedException e) {
            shoutDown();
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

    @Override
    public String getUuid() {
        return this.uuid;
    }
}
