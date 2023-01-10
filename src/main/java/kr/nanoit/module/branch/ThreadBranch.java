package kr.nanoit.module.branch;

import kr.nanoit.abst.ModuleProcess;
import kr.nanoit.db.auth.MessageService;
import kr.nanoit.domain.broker.InternalDataBranch;
import kr.nanoit.domain.broker.InternalDataOutBound;
import kr.nanoit.domain.broker.InternalDataSender;
import kr.nanoit.domain.broker.InternalDataType;
import kr.nanoit.domain.payload.PayloadType;
import kr.nanoit.module.auth.Auth;
import kr.nanoit.module.broker.Broker;
import kr.nanoit.module.inbound.socket.UserManager;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadBranch extends ModuleProcess {

    private final Auth auth;
    private final MessageService messageService;

    public ThreadBranch(Broker broker, String uuid, MessageService messageService, UserManager userManager) {
        super(broker, uuid);
        this.auth = new Auth(broker, userManager);
        this.messageService = messageService;
    }

    @Override
    public void run() {
        try {
            flag = true;
            while (flag) {
                Object object = broker.subscribe(InternalDataType.BRANCH);
                if (object != null && object instanceof InternalDataBranch) {
                    InternalDataBranch internalDataBranch = (InternalDataBranch) object;
                    PayloadType payloadType = internalDataBranch.getPayload().getType();

                    switch (payloadType) {
                        case AUTHENTICATION:
                            auth.verificationAccount(internalDataBranch, messageService);
                            break;
                        case SEND:
                            if (broker.publish(new InternalDataSender(internalDataBranch.getMetaData(), internalDataBranch.getPayload()))) {
                            }
                            break;
                        case REPORT_ACK:
                            if (broker.publish(new InternalDataOutBound(internalDataBranch.getMetaData(), internalDataBranch.getPayload()))) {
                                log.info("[BRANCH]   REPORT_ACK DATA TO OutBound => [TYPE : {} DATA : {}]", internalDataBranch.getPayload().getType(), internalDataBranch.getPayload());
                            }
                            break;
                        case ALIVE:
                            if (broker.publish(new InternalDataOutBound(internalDataBranch.getMetaData(), internalDataBranch.getPayload()))) {
                                log.info("[BRANCH]   ALIVE DATA TO OutBound => [TYPE : {} DATA : {}]", internalDataBranch.getPayload().getType(), internalDataBranch.getPayload());
                            }
                            break;
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
        flag = false;
        log.warn("[BRANCH   THIS THREAD SHUTDOWN]");
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
