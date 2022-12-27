package kr.nanoit.module.auth;


import com.fasterxml.jackson.databind.ObjectMapper;
import kr.nanoit.db.auth.MessageService;
import kr.nanoit.domain.broker.InternalDataBranch;
import kr.nanoit.domain.broker.InternalDataOutBound;
import kr.nanoit.domain.entity.AgentEntity;
import kr.nanoit.domain.payload.*;
import kr.nanoit.dto.MemberDto;
import kr.nanoit.module.broker.Broker;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;


@Slf4j
public class Auth {
    private final ObjectMapper objectMapper;
    private final Broker broker;

    public Auth(Broker broker) {
        this.broker = broker;
        this.objectMapper = new ObjectMapper();
    }


    //TODO 로직 추가 혹은 수정할 게 있는지 확인 및 작업 시작
    public void verificationAccount(InternalDataBranch internalDataBranch, MessageService messageService) {
        try {
            Payload payload = internalDataBranch.getPayload();
            Authentication authentication = objectMapper.convertValue(payload.getData(), Authentication.class);

            MemberDto userinfo = messageService.findUser(authentication.getUsername()).toDto();
            if (userinfo == null) {
                BadSend("Failed to Authentication", internalDataBranch);
            }
            if (isMatchedPw(authentication.getPassword(), userinfo.getPassword()) && userinfo.getEmail() == authentication.getEmail()) {
                AgentEntity agentEntity = messageService.findAgent(authentication.getAgent_id());
                if (agentEntity != null) {

                    if (messageService.isValidAccess(agentEntity.getAccess_list_id()) != false) {

                        if (messageService.updateAgentStatus(agentEntity.getId(), agentEntity.getMember_id(), "CONNECTED") != false) {
                            broker.publish(new InternalDataOutBound(internalDataBranch.getMetaData(), new Payload(PayloadType.AUTHENTICATION_ACK, internalDataBranch.getPayload().getMessageUuid(), new AuthenticationAck(authentication.getAgent_id(), "Connect Success"))));
                        } else {
                            BadSend("Server Error", internalDataBranch);
                        }
                    } else {
                        BadSend("Requested agent is not allowed agent", internalDataBranch);
                    }
                } else {
                    BadSend("Agent does not exist", internalDataBranch);
                }
            } else {
                BadSend("Failed to Authentication", internalDataBranch);
            }
        } catch (Exception e) {
            BadSend("Server Error", internalDataBranch);
            e.printStackTrace();
        }
    }


    private boolean isMatchedPw(String providedPassword, String comparePassword) {
        return BCrypt.checkpw(providedPassword, comparePassword);
    }

    private void BadSend(String reason, InternalDataBranch internalDataBranch) {
        if (broker.publish(new InternalDataOutBound(internalDataBranch.getMetaData(), new Payload(PayloadType.AUTHENTICATION_ACK, internalDataBranch.getPayload().getMessageUuid(), new ErrorPayload(reason))))) {
            log.warn("[AUTH] key={} {}}", internalDataBranch.getMetaData().getSocketUuid(), reason);
        }
    }

    private String bcryptPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}
