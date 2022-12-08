package kr.nanoit.module.auth;

// 오로지 auth 에 관해서만 , 인증 완료 후 Outbound 로 OutboundDto 로 변환
// from Branch , To Outbound

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.nanoit.domain.broker.InternalDataBranch;
import kr.nanoit.domain.broker.InternalDataOutBound;
import kr.nanoit.domain.payload.Authentication;
import kr.nanoit.domain.payload.Payload;
import kr.nanoit.domain.payload.PayloadType;
import kr.nanoit.domain.payload.Send;
import kr.nanoit.extension.Jackson;
import kr.nanoit.module.broker.Broker;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;


@Slf4j
public class Auth {
    private final String id = "test01";
    private final String pw = "q1w2e3";
    private final ObjectMapper objectMapper;

    public Auth() {
        this.objectMapper = Jackson.getInstance().getObjectMapper();
    }


    public void verificationAccount(InternalDataBranch internalDataBranch, Broker broker) {
        Payload payload = internalDataBranch.getPayload();
        Authentication authentication = objectMapper.convertValue(payload, Authentication.class);
        String identify = authentication.getUsername();
        String password = authentication.getPassword();
        if (isAccount(identify, password)) {
            // ACK 제작 후 OutBound
            broker.publish(new InternalDataOutBound(internalDataBranch.getMetaData(), new Payload(PayloadType.AUTHENTICATION_ACK, internalDataBranch.getPayload().getMessageUuid(), authentication)));
        }
    }

    public void verificationSendData(InternalDataBranch internalDataBranch, Broker broker){
        Payload payload = internalDataBranch.getPayload();
        Send send = objectMapper.convertValue(payload.getData(), Send.class);
        if (isPhoneNum(send.getPhone()) && isCallBack(send.getCallback())) {

        }
    }


    private boolean isAccount(String identify, String password) {
        if (identify.equals(id) && password.equals(pw)) {
            return true;
        }
        return false;
    }

    private boolean isPhoneNum(String num) {
        return Pattern.matches("^\\d{2,3}\\d{3,4}\\d{4}$", num);
    }

    private boolean isCallBack(String str) {
        return Pattern.matches("^\\d{2,3}\\d{3,4}\\d{4}$", str);
    }

//    private boolean isContent(String str){
//        if(str != null && str != "" && str)
//    }
}
