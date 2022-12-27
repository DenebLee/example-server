package kr.nanoit.db.auth;

import kr.nanoit.db.PostgreSqlDbcp;
import kr.nanoit.domain.entity.AgentEntity;
import kr.nanoit.domain.entity.MemberEntity;

import java.sql.SQLException;

public interface MessageService {
    static MessageService createPostgreSqL(PostgreSqlDbcp dbcp) {
        return new MessageServiceImpl(dbcp);
    }


    // memeber
    MemberEntity findUser(String username) throws SQLException;

    boolean insertUser(MemberEntity memberDto) throws SQLException;

    boolean containsById();


    // agent
    AgentEntity findAgent(long agentId);

    boolean insertAgent(AgentEntity agentEntity);

    boolean updateAgentStatus(long id, long memberId, String status);


    // access_list
    boolean isValidAccess(long accessListId);


    //

}



