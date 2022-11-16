package kr.nanoit.module.auth;

// 오로지 auth 에 관해서만 , 인증 완료 후 Outbound로 OutboundDto로 변환
// from Branch , To Outbound

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.nanoit.domain.broker.InternalDataBranch;
import kr.nanoit.module.broker.Broker;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class Auth {
    private final String id = "test01";
    private final String password = "test01";
    private final ObjectMapper objectMapper;

    public Auth() {
        this.objectMapper = new ObjectMapper();
    }

    public void verification(InternalDataBranch internalDataBranch, Broker broker) {
        log.info("");
    }
}
