package kr.nanoit.module.mapper;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.nanoit.abst.ModuleProcess;
import kr.nanoit.domain.broker.InternalDataFilter;
import kr.nanoit.domain.broker.InternalDataMapper;
import kr.nanoit.domain.broker.InternalDataType;
import kr.nanoit.domain.payload.*;
import kr.nanoit.module.broker.Broker;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadMapper extends ModuleProcess {

    private final ObjectMapper objectMapper;
    private InternalDataMapper internalDataMapper;

    public ThreadMapper(Broker broker, String uuid) {
        super(broker, uuid);
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void run() {
        this.flag = true;

        while (this.flag) {
            try {
                Object object = broker.subscribe(InternalDataType.MAPPER);
                if (object != null && object instanceof InternalDataMapper) {
                    InternalDataMapper internalDataMapper = (InternalDataMapper) object;
                    Payload payload = objectMapper.readValue(internalDataMapper.getPayload(), Payload.class);
                    switch (payload.getType()) {
                        case SEND:
                            Send send = objectMapper.convertValue(payload.getData(), Send.class);
                            if (broker.publish(new InternalDataFilter(internalDataMapper.getMetaData(), new Payload(payload.getType(), payload.getMessageUuid(), send)))) {
                                log.debug("[BRANCH]   SEND DATA TO FILTER => [TYPE : {} DATA : {}]", PayloadType.SEND, internalDataMapper.getPayload());
                            }
                            break;

                        case REPORT_ACK:
                            ReportAck reportAck = objectMapper.convertValue(payload.getData(), ReportAck.class);
                            if (broker.publish(new InternalDataFilter(internalDataMapper.getMetaData(), new Payload(payload.getType(), payload.getMessageUuid(), reportAck)))) {
                                log.debug("[BRANCH]   SEND DATA TO FILTER => [TYPE : {} DATA : {}]", PayloadType.REPORT_ACK, internalDataMapper.getPayload());
                            }
                            break;

                        case ALIVE:
                            break;

                        case AUTHENTICATION:
                            Authentication authentication = objectMapper.convertValue(payload.getData(), Authentication.class);
                            if (broker.publish(new InternalDataFilter(internalDataMapper.getMetaData(), new Payload(payload.getType(), payload.getMessageUuid(), authentication)))) {
                                log.debug("[BRANCH]   SEND DATA TO FILTER => [TYPE : {} DATA : {}]", PayloadType.AUTHENTICATION, internalDataMapper.getPayload());
                            }
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
        log.warn("[MAPPER]   THIS THREAD SHUTDOWN");
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
//  try {
//          Object object = broker.subscribe(InternalDataType.MAPPER);
//          if (object != null && object instanceof InternalDataMapper) {
//          internalDataMapper = (InternalDataMapper) object;
//          Payload payload = objectMapper.readValue(internalDataMapper.getPayload(), Payload.class);
//        if (broker.publish(new InternalDataFilter(internalDataMapper.getMetaData(), new Payload(payload.getType(), payload.getMessageUuid(), payload)))) {
//        log.debug("[MAPPER]   SEND DATA TO FILTER => [TYPE : {} DATA : {}]", payload.getType(), internalDataMapper.getPayload());
//        }
//        }
//        } catch (DataNullException | JsonProcessingException e) {
//        log.warn("[MAPPER] Error = {}", e.getMessage());
//        } catch (InterruptedException e) {
//        throw new RuntimeException(e);
//        } catch (Exception e) {
//        e.printStackTrace();
//        shoutDown();
//        }