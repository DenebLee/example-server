package kr.nanoit.db.query;

import kr.nanoit.domain.entity.AgentEntity;
import kr.nanoit.domain.entity.MemberEntity;
import kr.nanoit.dto.MemberDto;

public final class MessageServicePostgreSqlQuerys {

    // Member
    public static String findUser(String username) {
        return " SELECT id, username, password, email, created_at, last_modified_at FROM member WHERE username = '" + username + "'";
    }

    public static String insertUser(MemberEntity memberEntity) {
        return "INSERT INTO member (username, password, email, created_at, last_modified_at) VALUES ('" + memberEntity.getUsername() + "', '" + memberEntity.getPassword() + "', '" + memberEntity.getEamil() + "', '" + memberEntity.getCreated_at() + "', '" + memberEntity.getLast_modified_at() + "')";
    }


    // Agent
    public static String findAgent(long id) {
        return "SELECT * FROM agent WHERE id = '" + id + "' ";
    }

    public static String insertAgent(AgentEntity agentEntity) {
        return "INSERT INTO agent (id, member_id, access_list_id, status, created_at, last_modified_at) VALUES ('" + agentEntity.getId() + "', '" + agentEntity.getMember_id() + "', '" + agentEntity.getAccess_list_id() + "', '" + agentEntity.getStatus() + "', '" + agentEntity.getCreated_at() + "' , '" + agentEntity.getLast_modified_at() + "') ";
    }

    public static String updateAgentStatus(long id, long memberId, String status) {
        return "UPDATE agent SET status = '" + status + "' WHERE id = '" + id + "' AND member_id = '" + memberId + "' ";
    }

    public static String findAccessList(long accessListId) {
        return "SELECT * FROM access_list WHERE id = '" + accessListId + "' ";
    }
}
