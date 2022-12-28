package kr.nanoit.module.auth;


import com.fasterxml.jackson.databind.ObjectMapper;
import kr.nanoit.db.auth.AuthenticaionStatus;
import kr.nanoit.db.auth.MessageService;
import kr.nanoit.domain.VO.UserVO;
import kr.nanoit.domain.broker.InternalDataBranch;
import kr.nanoit.domain.broker.InternalDataOutBound;
import kr.nanoit.domain.entity.AgentEntity;
import kr.nanoit.domain.payload.*;
import kr.nanoit.dto.MemberDto;
import kr.nanoit.module.broker.Broker;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Timestamp;


@Slf4j
public class Auth {
    private final ObjectMapper objectMapper;
    private final Broker broker;

    public Auth(Broker broker) {
        this.broker = broker;
        this.objectMapper = new ObjectMapper();
    }


    public void verificationAccount(InternalDataBranch internalDataBranch, MessageService messageService) {
        try {
            Payload payload = internalDataBranch.getPayload();
            Authentication authentication = objectMapper.convertValue(payload.getData(), Authentication.class);

            MemberDto userinfo = messageService.findUser(authentication.getUsername()).toDto();
            if (userinfo == null) {
                BadSend("Threre're no Memeber data for authentication", internalDataBranch);
            }
            if (isMatchedPw(authentication.getPassword(), userinfo.getPassword()) == true) {
                AgentEntity agentEntity = messageService.findAgent(authentication.getAgent_id());
                if (agentEntity != null) {
                    if (messageService.isValidAccess(agentEntity.getAccess_list_id()) != false) {
                        if (agentEntity.getStatus() != "DISCONNECTED") {
                            if (messageService.updateAgentStatus(agentEntity.getId(), agentEntity.getMember_id(), "CONNECTED", new Timestamp(System.currentTimeMillis())) != false) {
                                UserVO userVO = new UserVO(userinfo.getUsername(), agentEntity.getId(), AuthenticaionStatus.COMPLETE);

                                broker.publish(new InternalDataOutBound(internalDataBranch.getMetaData(), new Payload(PayloadType.AUTHENTICATION_ACK, internalDataBranch.getPayload().getMessageUuid(), new AuthenticationAck(authentication.getAgent_id(), "Connect Success"))));
                            } else {
                                BadSend("Server Error", internalDataBranch);
                            }
                        } else {
                            BadSend("This Agent already connected", internalDataBranch);
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
        if (BCrypt.checkpw(providedPassword, comparePassword) == true) {
            return true;
        }
        return false;
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
