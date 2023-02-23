package kr.nanoit.module.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.nanoit.db.auth.AuthenticaionStatus;
import kr.nanoit.db.auth.MessageService;
import kr.nanoit.domain.broker.InternalDataBranch;
import kr.nanoit.domain.broker.InternalDataOutBound;
import kr.nanoit.domain.entity.AgentEntity;
import kr.nanoit.domain.entity.MemberEntity;
import kr.nanoit.domain.message.AgentStatus;
import kr.nanoit.domain.message.MessageResult;
import kr.nanoit.domain.payload.*;
import kr.nanoit.dto.MemberDto;
import kr.nanoit.dto.UserInfo;
import kr.nanoit.exception.FindFailedException;
import kr.nanoit.exception.ValidationException;
import kr.nanoit.extension.Jackson;
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
            Payload payload = Jackson.getInstance().getObjectMapper().convertValue(internalDataBranch.getPayload(), Payload.class);
            Authentication authentication = Jackson.getInstance().getObjectMapper().convertValue(payload.getData(), Authentication.class);
            MemberEntity user = messageService.findUser(authentication.getUsername());

            if (user == null) throw new FindFailedException("not found username=" + authentication.getUsername());

            MemberDto memberDto = user.toDto();

            if (!isMatchedPw(authentication.getPassword(), memberDto.getPassword())) {
                throw new ValidationException(internalDataBranch, "Authentication failure Account information verification required");
            }

            AgentEntity agentEntity = messageService.findAgent(authentication.getAgent_id());

            if (agentEntity.getStatus() != AgentStatus.DISCONNECTED) {
                throw new ValidationException(internalDataBranch, "This Agent already connected");
            }
            if (!messageService.updateAgentStatus(agentEntity.getId(), agentEntity.getMember_id(), AgentStatus.CONNECTED, new Timestamp(System.currentTimeMillis()))) {
                throw new ValidationException(internalDataBranch, "Server Error");
            }

            UserInfo userInfo = new UserInfo();
            userInfo.setAgent_id(agentEntity.getId())
                    .setMemberId(agentEntity.getMember_id())
                    .setUsername(memberDto.getUsername())
                    .setAuthenticaionStatus(AuthenticaionStatus.COMPLETE);

            userManager.registUser(internalDataBranch.UUID(), userInfo);

            if (broker.publish(new InternalDataOutBound(internalDataBranch.getMetaData(), new Payload(PayloadType.AUTHENTICATION_ACK, internalDataBranch.getPayload().getMessageUuid(), new AuthenticationAck(agentEntity.getId(), MessageResult.SUCCESS))))) {
                log.debug("[AUTH]   DATA TO FILTER => [TYPE : {} DATA : {}]", internalDataBranch.getPayload().getType(), internalDataBranch.getPayload());
            }

        } catch (ValidationException e) {
            sendAuthenticationResult(e.getReason(), e.getInternalDataBranch());
            log.warn("[FILTER] @USER:{} DataNullException Call  {} ", e.getInternalDataBranch().UUID(), e.getReason());
        } catch (FindFailedException e) {
            sendAuthenticationResult("Authentication failure Account information verification required", internalDataBranch);
            log.warn("[FILTER] FindFailedException Call  {} ", e.getReason());
        } catch (Exception e) {
            e.printStackTrace();
            sendAuthenticationResult("unknown error", internalDataBranch);
        }
    }

    private boolean isMatchedPw(String providedPassword, String comparePassword) {
        if (BCrypt.checkpw(providedPassword, comparePassword) == true) {
            return true;
        }
        return false;
    }

    private void sendAuthenticationResult(String reason, InternalDataBranch internalDataBranch) {
        broker.publish(new InternalDataOutBound(internalDataBranch.getMetaData(), new Payload(PayloadType.AUTHENTICATION_ACK, internalDataBranch.getPayload().getMessageUuid(), new ErrorPayload(reason))));
    }

    private String bcryptPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}
