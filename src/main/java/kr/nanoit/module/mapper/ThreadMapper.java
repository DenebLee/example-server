package kr.nanoit.module.mapper;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jdk.jfr.events.ExceptionStatisticsEvent;
import kr.nanoit.abst.ModuleProcess;
import kr.nanoit.domain.broker.InternalDataFilter;
import kr.nanoit.domain.broker.InternalDataMapper;
import kr.nanoit.domain.broker.InternalDataType;
import kr.nanoit.domain.payload.Authentication;
import kr.nanoit.domain.payload.Payload;
import kr.nanoit.domain.payload.Send;
import kr.nanoit.module.broker.Broker;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

// Mapper
@Slf4j
public class ThreadMapper extends ModuleProcess {

    private final ObjectMapper objectMapper;

    public ThreadMapper(Broker broker, String uuid) {
        super(broker, uuid);
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void run() {
        this.flag = true;
        while (this.flag) {
            Object object;
            try {
                object = broker.subscribe(InternalDataType.MAPPER);
                if (object != null && object instanceof InternalDataMapper) {
                    InternalDataMapper internalDataMapper = (InternalDataMapper) object;
                    Payload payload = objectMapper.readValue(internalDataMapper.getPayload(), Payload.class);
                    switch (payload.getType()) {
                        case SEND:
                            Send send = objectMapper.convertValue(payload.getData(), Send.class);
                            broker.publish(new InternalDataFilter(internalDataMapper.getMetaData(), new Payload(payload.getType(), payload.getMessageUuid(), send)));
                            break;

                        case REPORT_ACK:
                            break;

                        case ALIVE:
                            break;

                        case AUTHENTICATION:
                            Authentication authentication = objectMapper.convertValue(payload.getData(), Authentication.class);
                            broker.publish(new InternalDataFilter(internalDataMapper.getMetaData(), new Payload(payload.getType( ), payload.getMessageUuid(), authentication)));
                    }


                }
            } catch (InterruptedException | JsonProcessingException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {
                e.printStackTrace();
                shoutDown();
            }
        }
    }

    @Override
    public void shoutDown() {
        this.flag = false;
        log.warn("[MAPPER   THIS THREAD SHUTDOWN]");
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
