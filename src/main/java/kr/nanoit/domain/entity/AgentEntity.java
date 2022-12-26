package kr.nanoit.domain.entity;


import lombok.*;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString


public class AgentEntity {
    private long agent_id;
    private long memeber_id;
    private long access_list_id;
    private String status;
    private Timestamp created_at;
    private Timestamp last_modified_at;
}
