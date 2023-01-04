package kr.nanoit.domain.entity;


import kr.nanoit.domain.message.AgentStatus;
import kr.nanoit.dto.AgentDto;
import lombok.*;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString


public class AgentEntity {
    private long id;
    private long member_id;
    private long access_list_id;
    private AgentStatus status;
    private Timestamp created_at;
    private Timestamp last_modified_at;

    public AgentDto toDto() {
        return new AgentDto(id, member_id, access_list_id, status, created_at, last_modified_at);
    }
}
