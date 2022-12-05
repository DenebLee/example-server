package kr.nanoit.module.filter;

import kr.nanoit.abst.ModuleProcess;
import kr.nanoit.domain.broker.*;
import kr.nanoit.domain.payload.ErrorDto;
import kr.nanoit.domain.payload.Payload;
import kr.nanoit.domain.payload.PayloadType;
import kr.nanoit.module.broker.Broker;
import lombok.extern.slf4j.Slf4j;

// Filter
@Slf4j
public class ThreadFilter extends ModuleProcess {


    public ThreadFilter(Broker broker, String uuid) {
        super(broker, uuid);
    }

    @Override
    public void run() {
        try {
            this.flag = true;
            while (this.flag) {
                Object object = broker.subscribe(InternalDataType.FILTER);
                if (object != null && object instanceof InternalDataFilter) {
//                    log.info("[FILTER]   DATA INPUT => [{}]", object);
                    InternalDataFilter internalDataFilter = (InternalDataFilter) object;

                    if (internalDataFilter.getMetaData() != null && internalDataFilter.getPayload().getData() != null) {
                        if (broker.publish(new InternalDataBranch(internalDataFilter.getMetaData(), new Payload(internalDataFilter.getPayload().getType(), internalDataFilter.getPayload().getMessageUuid(), internalDataFilter.getPayload().getData())))) {
//                            log.info("[FILTER]   TO BRANCH => [TYPE : {} DATA : {}]", internalDataFilter.getMetaData(), internalDataFilter.getPayload().getData());
                        }
                    } else {
                        if (broker.publish(new InternalDataOutBound(internalDataFilter.getMetaData(), new Payload(PayloadType.BAD_SEND, internalDataFilter.getPayload().getMessageUuid(), new ErrorDto("Data null"))))) {
                            log.error("[FILTER]   There is null data ");
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
        log.warn("[FILTER   THIS THREAD SHUTDOWN]");
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
