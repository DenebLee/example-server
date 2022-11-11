package kr.nanoit.module.branch;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.nanoit.domain.broker.InternalDataBranch;
import kr.nanoit.domain.broker.InternalDataType;
import kr.nanoit.domain.payload.Authentication;
import kr.nanoit.domain.payload.Payload;
import kr.nanoit.domain.payload.PayloadType;
import kr.nanoit.module.borker.Broker;

/**
 * filter에서 받은 정제된 데이터를 목적에 맞게 각 모듈로 분배하는 분배기 역할
 * - to outbound
 * - to sender
 * - to 어디로 보내기
 */
public class Branch implements Runnable {
    private Broker broker;
    private final ObjectMapper objectMapper;

    public Branch(Broker broker) {
        this.broker = broker;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void run() {
        try {
            Object object;
            Payload payload;
            while (true) {
                object = broker.subscribe(InternalDataType.BRANCH);
                if (object instanceof InternalDataBranch) {
                    InternalDataBranch internalDataBranch = (InternalDataBranch) object;
                    if (internalDataBranch.getPayload().getType().equals(PayloadType.AUTHENTICATION)) {
                        Authentication authentication = objectMapper.convertValue(internalDataBranch.getPayload().getData(), Authentication.class);
                        // 만들어진 authentication Dto Auth 로 보냄

                    } else if (internalDataBranch.getPayload().getType().equals(PayloadType.SEND)) {


                    } else if (internalDataBranch.getPayload().getType().equals(PayloadType.SEND_ACK)) {


                    } else if (internalDataBranch.getPayload().getType().equals(PayloadType.REPORT)) {


                    } else if (internalDataBranch.getPayload().getType().equals(PayloadType.REPORT_AC)) {


                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
