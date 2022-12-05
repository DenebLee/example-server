package kr.nanoit.module.outbound;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.nanoit.abst.ModuleProcess;
import kr.nanoit.domain.broker.InternalDataOutBound;
import kr.nanoit.domain.payload.Payload;
import kr.nanoit.extension.Jackson;
import kr.nanoit.module.broker.Broker;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

@Slf4j
public class ThreadOutBound extends ModuleProcess {

    private final ObjectMapper objectMapper;
    private BufferedReader bufferedReader;

    public ThreadOutBound(Broker broker, String uuid) throws IOException {
        super(broker, uuid);
        this.objectMapper = Jackson.getInstance().getObjectMapper();
    }

    @Override
    public void run() {
        try {
            this.flag = true;
            while (this.flag) {
                if (connectCarrier.isConnected()) {
                    InternalDataOutBound internalDataOutBound = objectMapper.readValue(bufferedReader.readLine(), InternalDataOutBound.class);

                    String payload = toJSON(internalDataOutBound.getPayload());
//                    log.info("[OUTBOUND] DATA INPUT => {}", object);
                    switch (internalDataOutBound.getPayload().getType()) {

                        // ReportACK? SEND_ACK, ALIVE_ACK, BAD_SEND,AUTHENTICATION_ACK

                        case SEND_ACK:
                            broker.outBound(internalDataOutBound.getMetaData().getSocketUuid(), payload);
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
        } catch (JsonProcessingException e) {
            shoutDown();
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
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
