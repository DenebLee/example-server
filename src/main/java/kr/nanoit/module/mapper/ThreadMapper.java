package kr.nanoit.module.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import kr.nanoit.abst.NanoItThread;
import kr.nanoit.domain.broker.InternalDataFilter;
import kr.nanoit.domain.broker.InternalDataMapper;
import kr.nanoit.domain.broker.InternalDataType;
import kr.nanoit.domain.payload.Payload;
import kr.nanoit.module.broker.Broker;
import lombok.extern.slf4j.Slf4j;

// Mapper
@Slf4j
public class ThreadMapper extends NanoItThread {

    public ThreadMapper(Broker broker, String uuid) {
        super(broker, uuid);
    }

    @Override
    public void execute() {
        this.flag = true;
        while (this.flag) {
            Object object;
            try {
                object = broker.subscribe(InternalDataType.MAPPER);
                if (object != null && object instanceof InternalDataMapper) {
//                    log.info("[MAPPER]   READ-THREAD DATA INPUT => [{}]", object);
                    InternalDataMapper internalDataMapper = (InternalDataMapper) object;
                    Payload payload = objectMapper.readValue(internalDataMapper.getPayload(), Payload.class);

                    if (broker.publish(new InternalDataFilter(internalDataMapper.getMetaData(), payload))) {
//                        log.info("[MAPPER]   TO FILTER => [TYPE : {} DATA : {}]", payload.getType(), payload.getData());
                    } else {
                        log.error("[MAPPER]   NOT FOUND DATA => [{}]", payload);
                    }
                }

            } catch (InterruptedException | JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void shoutDown() {
        this.flag = false;
        log.warn("[MAPPER   THIS THREAD SHUTDOWN]");
    }

    @Override
    public Thread.State getState() {
        return this.thread.getState();
    }

    @Override
    public void sleep() throws InterruptedException {
        Thread.sleep(1000);
    }
}
