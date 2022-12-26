package kr.nanoit.db.auth;

import kr.nanoit.db.PostgreSqlDbcp;
import kr.nanoit.domain.entity.AgentEntity;
import kr.nanoit.domain.entity.MemberEntity;
import kr.nanoit.dto.UserDto;

import java.sql.SQLException;

public interface MessageService {
    static MessageService createPostgreSqL(PostgreSqlDbcp dbcp) {
        return new MessageServiceImpl(dbcp);
    }


    // memeber
    MemberEntity findUser(String username) throws SQLException;

    boolean saveUser(UserDto userDto) throws SQLException;

    boolean containsById();


    // agent
    AgentEntity findAgent(long agentId);

    boolean updateAgentStatus(long agentId, String status);

    // access_list
    boolean isValidAccess(long accessListId);

}



