package kr.nanoit.module.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import kr.nanoit.abst.ModuleProcess;
import kr.nanoit.domain.broker.*;
import kr.nanoit.domain.payload.ErrorPayload;
import kr.nanoit.domain.payload.Payload;
import kr.nanoit.domain.payload.PayloadType;
import kr.nanoit.exception.DataNullException;
import kr.nanoit.extension.Jackson;
import kr.nanoit.extension.Validation;
import kr.nanoit.module.broker.Broker;
import kr.nanoit.module.inbound.socket.UserManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadFilter extends ModuleProcess {
    private final UserManager userManager;
    private final Validation validation;
    private InternalDataFilter internalDataFilter;

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
                    internalDataFilter = (InternalDataFilter) object;

                    if (internalDataFilter.getMetaData() == null) {
                        throw new DataNullException("MetaData is null");
                    }
                    if (internalDataFilter.getPayload().getType() == null) {
                        throw new DataNullException("Payload.Type is null");
                    }

                    if (internalDataFilter.getPayload().getMessageUuid() == null) {
                        throw new DataNullException("MessageUuid is null");
                    }

                    if (internalDataFilter.getPayload().getData() == null) {
                        throw new DataNullException("Payload.Data is null");
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
                        log.debug("[FILTER]   DATA TO BRANCH => [TYPE : {} DATA : {}]", internalDataFilter.getPayload().getType(), internalDataFilter.getPayload());
                    }
                }
            }
        } catch (DataNullException e) {
            publishBadRequest(internalDataFilter, e.getReason());
            log.warn("[FILTER] @USER:{}] DataNullException Call  {} ", internalDataFilter.UUID(), e.getReason());
        } catch (InterruptedException e) {
            shoutDown();
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void publishBadRequest(InternalDataFilter internalDataFilter, String str) {
        broker.publish(new InternalDataOutBound(internalDataFilter.getMetaData(), new Payload(PayloadType.BAD_SEND, internalDataFilter.getPayload().getMessageUuid(), new ErrorPayload(str))));
    }

    @Override
    public void shoutDown() {
        this.flag = false;
        log.warn("[FILTER]   THIS THREAD SHUTDOWN");
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
