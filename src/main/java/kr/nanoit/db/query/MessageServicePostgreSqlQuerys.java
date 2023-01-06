package kr.nanoit.db.query;

import kr.nanoit.domain.entity.AgentEntity;
import kr.nanoit.domain.entity.ClientMessageEntity;
import kr.nanoit.domain.entity.CompanyMessageEntity;
import kr.nanoit.domain.entity.MemberEntity;
import kr.nanoit.domain.message.AgentStatus;
import kr.nanoit.domain.message.MessageStatus;

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
    public static String findClientMessage(long id) {
        return "SELECT * FROM client_message WHERE id = '" + id + "' ";
    }

    public static String deleteClientMessage() {
        return null;
    }

    public static String insertClientMessage(ClientMessageEntity clientMessageEntity) {
        return "INSERT INTO client_message (client_message_id,relay_company_id,type,status,send_time,sender_num,sender_callback,sender_name,content,created_at, last_modified_at) VALUES ('" + clientMessageEntity.getAgent_id() +
                "', '" + clientMessageEntity.getType() + "', '" + clientMessageEntity.getStatus() + "', '" + clientMessageEntity.getSend_time() + "', '" + clientMessageEntity.getSender_num() + "', '" + clientMessageEntity.getSender_callback() + "', '" +
                "" + clientMessageEntity.getSender_name() + "', '" + clientMessageEntity.getContent() + "', '" + clientMessageEntity.getCreated_at() + "', '" + clientMessageEntity.getLast_modified_at() + "')";
    }

    public static String updateMessageStatus(MessageStatus messageStatus) {
        return "UPDATE client_message SET status = '" + messageStatus + "' ";
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

    public static String insertCompanyMessage(CompanyMessageEntity companyMessageEntity) {
        return "INSERT INTO company_message (client_message_id,relay_company_id,type,status,send_time,sender_num,sender_callback,sender_name,content,created_at,last_modified_at) VALUES" +
                " ('" + companyMessageEntity.getClient_message_id() + "','" + companyMessageEntity.getRelay_company_id() + "','" + companyMessageEntity.getType() + "'," +
                "'" + companyMessageEntity.getStatus() + "','" + companyMessageEntity.getSend_time() + "','" + companyMessageEntity.getSender_num() + "'," +
                "'" + companyMessageEntity.getSender_callback() + "','" + companyMessageEntity.getSender_name() + "','" + companyMessageEntity.getContent() + "','" + companyMessageEntity.getCreated_at() + "','" + companyMessageEntity.getLast_modified_at() + "')";
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
