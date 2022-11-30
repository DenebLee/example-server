package kr.nanoit.module.branch;

import kr.nanoit.abst.Process;
import kr.nanoit.domain.broker.InternalDataBranch;
import kr.nanoit.domain.broker.InternalDataOutBound;
import kr.nanoit.domain.broker.InternalDataSender;
import kr.nanoit.domain.broker.InternalDataType;
import kr.nanoit.domain.payload.PayloadType;
import kr.nanoit.module.auth.Auth;
import kr.nanoit.module.broker.Broker;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 * filter에서 받은 정제된 데이터를 목적에 맞게 각 모듈로 분배하는 분배기 역할
 * - to outbound
 * - to sender
 * - to 어디로 보내기
 */
@Slf4j
public class Branch implements Process {
    private Broker broker;
    public Auth auth;
    private boolean flag;

    public Branch(Broker broker) {
        this.broker = broker;
        this.auth = new Auth();
    }

    @Override
    public void run() {
        try {
            flag =true;
            Object object;
            while (flag) {
                object = broker.subscribe(InternalDataType.BRANCH);
                if (object != null && object instanceof InternalDataBranch) {
//                    log.info("[BRANCH]   DATA INPUT => {}", object);
                    InternalDataBranch internalDataBranch = (InternalDataBranch) object;
                    PayloadType payloadType = internalDataBranch.getPayload().getType();

                    if (payloadType.equals(PayloadType.AUTHENTICATION)) {
                        auth.verification(internalDataBranch, broker);

                    } else if (payloadType.equals(PayloadType.SEND)) {
                        if (broker.publish(new InternalDataSender(internalDataBranch.getMetaData(), internalDataBranch.getPayload()))) {
//                            log.info("[BRANCH]   SEND DATA TO SENDER => [TYPE : {} DATA : {}]", internalDataBranch.getPayload().getType(), internalDataBranch.getPayload());
                        }

                    } else if (payloadType.equals(PayloadType.REPORT_ACK)) {
                        if (broker.publish(new InternalDataOutBound(internalDataBranch.getMetaData(), internalDataBranch.getPayload()))) {
                            log.info("[BRANCH]   REPORT_ACK DATA TO OutBound => [TYPE : {} DATA : {}]", internalDataBranch.getPayload().getType(), internalDataBranch.getPayload());
                        }

                    } else if (payloadType.equals(PayloadType.ALIVE)) {
                        if (broker.publish(new InternalDataOutBound(internalDataBranch.getMetaData(), internalDataBranch.getPayload()))) {
                            log.info("[BRANCH]   ALIVE DATA TO OutBound => [TYPE : {} DATA : {}]", internalDataBranch.getPayload().getType(), internalDataBranch.getPayload());
                        }
                    }
                }
            }
        } catch (InterruptedException e) {
            flag = false;
            e.printStackTrace();
        }
    }

    @Override
    public String getUuid() {
        return UUID.randomUUID().toString().substring(0, 7);
    }
}
