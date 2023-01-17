package kr.nanoit.module.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.nanoit.db.auth.AuthenticaionStatus;
import kr.nanoit.db.auth.MessageService;
import kr.nanoit.domain.broker.InternalDataBranch;
import kr.nanoit.domain.broker.InternalDataOutBound;
import kr.nanoit.domain.entity.AgentEntity;
import kr.nanoit.domain.entity.MemberEntity;
import kr.nanoit.domain.message.AgentStatus;
import kr.nanoit.domain.payload.*;
import kr.nanoit.dto.MemberDto;
import kr.nanoit.dto.UserInfo;
import kr.nanoit.exception.FindFailedException;
import kr.nanoit.module.broker.Broker;
import kr.nanoit.module.inbound.socket.UserManager;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Timestamp;

@Slf4j
public class Auth {
    private final ObjectMapper objectMapper;
    private final Broker broker;
    private final UserManager userManager;

    public Auth(Broker broker, UserManager userManager) {
        this.broker = broker;
        this.userManager = userManager;
        this.objectMapper = new ObjectMapper();
    }

    public void verificationAccount(InternalDataBranch internalDataBranch, MessageService messageService) {
        try {
            Payload payload = internalDataBranch.getPayload();
            Authentication authentication = objectMapper.convertValue(payload.getData(), Authentication.class);
            MemberEntity user = messageService.findUser(authentication.getUsername());
            if (user == null) throw new FindFailedException("not found username = " + authentication.getUsername());

            MemberDto memberDto = user.toDto();

            if (!isMatchedPw(authentication.getPassword(), memberDto.getPassword())) {
                sendAuthenticationResult("Failed to Authentication", internalDataBranch, null);
            }

            AgentEntity agentEntity = messageService.findAgent(authentication.getAgent_id());

            if (agentEntity == null || agentEntity.getId() == 0) {
                sendAuthenticationResult("Agent does not exist", internalDataBranch, null);
            }
            if (agentEntity.getStatus() != AgentStatus.DISCONNECTED) {
                sendAuthenticationResult("This Agent already connected", internalDataBranch, null);
            }

            if (!messageService.updateAgentStatus(agentEntity.getId(), agentEntity.getMember_id(), AgentStatus.CONNECTED, new Timestamp(System.currentTimeMillis()))) {
                sendAuthenticationResult("Server Error", internalDataBranch, null);
            }

            UserInfo userInfo = new UserInfo();
            userInfo.setAgent_id(agentEntity.getId())
                    .setMemberId(agentEntity.getMember_id())
                    .setUsername(memberDto.getUsername())
                    .setAuthenticaionStatus(AuthenticaionStatus.COMPLETE);

            userManager.registUser(internalDataBranch.UUID(), userInfo);

            if (broker.publish(new InternalDataOutBound(internalDataBranch.getMetaData(), new Payload(PayloadType.AUTHENTICATION_ACK, internalDataBranch.getPayload().getMessageUuid(), new AuthenticationAck(agentEntity.getId(), "Authentication Success"))))) {
            }

        } catch (FindFailedException e) {
            sendAuthenticationResult("Authentication failure Account information verification required", internalDataBranch, e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isMatchedPw(String providedPassword, String comparePassword) {
        if (BCrypt.checkpw(providedPassword, comparePassword) == true) {
            return true;
        }
        return false;
    }

    private void sendAuthenticationResult(String reason, InternalDataBranch internalDataBranch, Exception exception) {
        if (broker.publish(new InternalDataOutBound(internalDataBranch.getMetaData(), new Payload(PayloadType.AUTHENTICATION_ACK, internalDataBranch.getPayload().getMessageUuid(), new ErrorPayload(reason))))) {
            log.warn("[AUTH] key={} {}", internalDataBranch.getMetaData().getSocketUuid(), reason, exception);
        }
    }

    private String bcryptPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}
