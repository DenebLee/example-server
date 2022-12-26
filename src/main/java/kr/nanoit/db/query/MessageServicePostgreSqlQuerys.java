package kr.nanoit.db.query;

import kr.nanoit.dto.UserDto;

public final class MessageServicePostgreSqlQuerys {

    public static String findUser(String username) {
        return " SELECT id,username, password, email FROM member WHERE username = '" + username + "'";
    }

    public static String insertUser(UserDto userDto) {
        return "INSERT INTO member (username, password, email, created_at, last_modified_at) VALUES ('" + userDto.getUsername() + "', '" + userDto.getPassword() + "', '" + userDto.getEmail() + "', '" + userDto.getCreated_at() + "', '" + userDto.getLast_modified_at() + "')";
    }

    public static String findAgent(long agentId) {
        return "SELECT * FROM agent WHERE id = '" + agentId + "' ";
    }

    public static String updateAgentStatus(long agentId, String status) {
        return "UPDATE agent SET status = '" + status + "' WHERE id = '" + agentId + "' ";
    }

    public static String findAccessList(long accessListId) {
        return "SELECT * FROM access_list WHERE id = '" + accessListId + "' ";
    }
}
