package kr.nanoit.db.auth;

import kr.nanoit.db.PostgreSqlDbcp;
import kr.nanoit.domain.entity.AgentEntity;
import kr.nanoit.domain.entity.ClientMessageEntity;
import kr.nanoit.domain.entity.CompanyMessageEntity;
import kr.nanoit.domain.entity.MemberEntity;
import kr.nanoit.domain.message.AgentStatus;
import kr.nanoit.domain.message.MessageStatus;
import kr.nanoit.exception.FindFailedException;
import kr.nanoit.exception.InsertFailedException;
import kr.nanoit.exception.UpdateFailedException;

import java.sql.Timestamp;

public interface MessageService {
    static MessageService createPostgreSqL(PostgreSqlDbcp dbcp) {
        return new MessageServiceImpl(dbcp);
    }


    // Member
    MemberEntity findUser(String username) throws FindFailedException;

    boolean insertUser(MemberEntity memberDto) throws InsertFailedException;

    boolean containsById();


    // Agent
    AgentEntity findAgent(long agentId) throws FindFailedException;

    boolean insertAgent(AgentEntity agentEntity) throws InsertFailedException;

    boolean updateAgentStatus(long id, long memberId, AgentStatus status, Timestamp updateTime) throws UpdateFailedException;


    // Access_List
    boolean isValidAccess(long accessListId);


    // Client_Message
    ClientMessageEntity findClientMessage(long id) throws FindFailedException;

    boolean deleteClientMessage(long id);

    long insertClientMessage(ClientMessageEntity clientMessageEntity) throws InsertFailedException;

    boolean updateMessageStatus(long id, MessageStatus messageStatus) throws UpdateFailedException;


    // Company_Message
    CompanyMessageEntity findCompanyMessage();

    boolean deleteCompanyMessage();

    boolean updateCompanyMessage();

    boolean insertCompanyMessage(CompanyMessageEntity companyMessageEntity) throws InsertFailedException;

    int getCountMessageList();


    // Access_list
    boolean insertAccessList(long id, String address);

    boolean updateAccessList(long id, long replaceId);

    // Agent_status
    boolean insertAgentStatus(String status1, String status2);

    // Message_type
    boolean insertMessageType(String type);

    // Message_status
    boolean insertMessageStatus(String status1, String status2);

    boolean insertRelayCompany() throws InsertFailedException;

    // For Test
    void dropMessageTable();

    void deleteClientMessageTable();

    void deleteCompanyMessageTable();


}



