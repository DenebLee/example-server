package kr.nanoit.module.filter;

import kr.nanoit.abst.ModuleProcess;
import kr.nanoit.domain.broker.InternalDataBranch;
import kr.nanoit.domain.broker.InternalDataFilter;
import kr.nanoit.domain.broker.InternalDataOutBound;
import kr.nanoit.domain.broker.InternalDataType;
import kr.nanoit.domain.payload.ErrorPayload;
import kr.nanoit.domain.payload.Payload;
import kr.nanoit.domain.payload.PayloadType;
import kr.nanoit.extension.Validation;
import kr.nanoit.module.broker.Broker;
import kr.nanoit.module.inbound.socket.UserManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadFilter extends ModuleProcess {
    private final UserManager userManager;
    private final Validation validation;

    public ThreadFilter(Broker broker, String uuid, UserManager userManager) {
        super(broker, uuid);
        validation = new Validation();
        this.userManager = userManager;
    }

    @Override
    public void run() {
        try {
            this.flag = true;
            while (this.flag) {
                Object object = broker.subscribe(InternalDataType.FILTER);
                if (object instanceof InternalDataFilter) {
                    InternalDataFilter internalDataFilter = (InternalDataFilter) object;
                    if (internalDataFilter.getMetaData() == null) {
                        publishBadRequest(internalDataFilter, "MetaData is null");
                    }
                    if (internalDataFilter.getPayload().getMessageUuid() == null) {
                        publishBadRequest(internalDataFilter, "MessageUuid is null");
                    }
                    if (internalDataFilter.getPayload().getType() == null) {
                        publishBadRequest(internalDataFilter, "Payload.Type is null");
                    }
                    if (internalDataFilter.getPayload().getData() == null) {
                        publishBadRequest(internalDataFilter, "Payload.Data is null");
                    }
                    switch (internalDataFilter.getPayload().getType()) {
                        case SEND:
                            if (!validation.verificationSendData(internalDataFilter, userManager)) {
                                publishBadRequest(internalDataFilter, "Invalid Send value");
                            }
                            break;

                        case REPORT_ACK:
                            if (!validation.verificationReport_ackData(internalDataFilter)) {
                                publishBadRequest(internalDataFilter, "Invalid Report_ack value");
                            }
                            break;

                        case ALIVE:
                            if (!validation.verificationAliveData(internalDataFilter)) {
                                publishBadRequest(internalDataFilter, "Invalid Alive value");
                            }
                            break;
                    }
                    if (broker.publish(new InternalDataBranch(internalDataFilter.getMetaData(), internalDataFilter.getPayload()))) {
                    }
                }
            }
        } catch (InterruptedException e) {
            shoutDown();
            e.printStackTrace();
        }
    }

    private void publishBadRequest(InternalDataFilter internalDataFilter, String str) {
        if (broker.publish(new InternalDataOutBound(internalDataFilter.getMetaData(), new Payload(PayloadType.BAD_SEND, internalDataFilter.getPayload().getMessageUuid(), new ErrorPayload(str))))) {
            log.error("[FILTER]   There is null data ");
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
