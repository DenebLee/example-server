package kr.nanoit.dto;

import kr.nanoit.domain.entity.AgentEntity;
import kr.nanoit.domain.message.AgentStatus;
import lombok.*;
import lombok.experimental.Accessors;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Accessors(chain = true)

public class AgentDto {
    private long id;
    private long member_id;
    private long access_list_id;
    private AgentStatus status;
    private Timestamp created_at;
    private Timestamp last_modified_at;


    public AgentEntity toEntity() {
        return new AgentEntity(id, member_id, access_list_id, status, created_at, last_modified_at);
    }
}
