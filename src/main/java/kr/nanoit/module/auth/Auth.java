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
    private final ObjectMapper objectMapper;

    public Auth() {
        this.objectMapper = new ObjectMapper();
    }


    public void verificationAccount(InternalDataBranch internalDataBranch, Broker broker) {
        Payload payload = internalDataBranch.getPayload();
        Authentication authentication = objectMapper.convertValue(payload, Authentication.class);
        String identify = authentication.getUsername();
        String password = authentication.getPassword();
//        if (isAccount(identify, password)) {
//            broker.publish(new InternalDataOutBound(internalDataBranch.getMetaData(), new Payload(PayloadType.AUTHENTICATION_ACK, internalDataBranch.getPayload().getMessageUuid(), authentication)));
//        }
    }


    private boolean isAccount() {
        return false;
    }


//    private boolean isContent(String str){
//        if(str != null && str != "" && str)
//    }
}
