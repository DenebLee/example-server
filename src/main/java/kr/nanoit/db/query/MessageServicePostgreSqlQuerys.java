package kr.nanoit.db.query;

import kr.nanoit.domain.entity.AgentEntity;
import kr.nanoit.domain.entity.MemberEntity;
import kr.nanoit.domain.message.AgentStatus;

import java.sql.Timestamp;

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

    public static String updateAgentStatus(long id, long memberId, AgentStatus status, Timestamp updateTime) {
        return "UPDATE agent SET status = '" + status + "', last_modified_at = '" + updateTime + "'  WHERE id = '" + id + "' AND member_id = '" + memberId + "' ";
    }

    // Acess_list
    public static String findAccessList(long accessListId) {
        return "SELECT * FROM access_list WHERE id = '" + accessListId + "' ";
    }


    // Client_message
    public static String findClientMessage() {
        return null;
    }

    public static String deleteClientMessage() {
        return null;
    }

    public static String updateClientMessage() {
        return null;
    }

    public static String insertClientMessage() {
        return null;
    }


    // Company_message
    public static String findCompanyMessage() {
        return null;
    }

    public static String deleteCompanyMessage() {
        return null;
    }

    public static String updateCompanyMessage() {
        return null;
    }

    public static String insertCompanyMessage() {
        return null;
    }


    // Access_list
    public static String insertAccessList(long id, String address) {
        return "INSERT INTO access_list(id,address) VALUES ('" + id + "', '" + address + "') ";
    }

    public static String updateAccessList(long id, long replaceId) {
        return "UPDATE access_list SET id = '" + replaceId + "'  WHERE id = '" + id + "' ";
    }


    // Agent_status
    public static String insertAgentStatus(String status1, String status2) {
        return "INSERT INTO agent_status (status) VALUES ('" + status1 + "'),('" + status2 + "') ";
    }


    // Message_type
    public static String insertMessageType(String type) {
        return "INSERT INTO message_type (type) VALUES ('" + type + "') ";
    }


    // Message_status
    public static String insertMessageStatus(String status1, String status2) {
        return "INSERT INTO message_status (status) VALUES ('" + status1 + "'),('" + status2 + "')";
    }
}
