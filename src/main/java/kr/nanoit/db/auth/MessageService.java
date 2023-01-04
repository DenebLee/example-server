package kr.nanoit.db.auth;

import kr.nanoit.db.PostgreSqlDbcp;
import kr.nanoit.domain.entity.AgentEntity;
import kr.nanoit.domain.entity.ClientMessageEntity;
import kr.nanoit.domain.entity.CompanyMessageEntity;
import kr.nanoit.domain.entity.MemberEntity;
import kr.nanoit.domain.message.AgentStatus;
import kr.nanoit.exception.FindFailedException;

import java.sql.SQLException;
import java.sql.Timestamp;

public interface MessageService {
    static MessageService createPostgreSqL(PostgreSqlDbcp dbcp) {
        return new MessageServiceImpl(dbcp);
    }


    // Member
    MemberEntity findUser(String username) throws FindFailedException;

    boolean insertUser(MemberEntity memberDto) throws SQLException;

    boolean containsById();


    // Agent
    AgentEntity findAgent(long agentId);

    boolean insertAgent(AgentEntity agentEntity);

    boolean updateAgentStatus(long id, long memberId, AgentStatus status, Timestamp updateTime);


    // Access_List
    boolean isValidAccess(long accessListId);


    // Client_Message
    ClientMessageEntity findClientMessage();

    boolean deleteClientMessage();

    boolean updateClientMessage();

    boolean insertClientMessage();


    // Company_Message
    CompanyMessageEntity findCompanyMessage();

    boolean deleteCompanyMessage();

    boolean updateCompanyMessage();

    boolean insertCompanyMessage();


    // Access_list
    boolean insertAccessList(long id, String address);

    boolean updateAccessList(long id, long replaceId);

    // Agent_status
    boolean insertAgentStatus(String status1, String status2);

    // Message_type
    boolean insertMessageType(String type);

    // Message_status
    boolean insertMessageStatus(String status1, String status2);

}



