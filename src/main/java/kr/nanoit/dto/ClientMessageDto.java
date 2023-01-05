package kr.nanoit.dto;

import kr.nanoit.domain.entity.ClientMessageEntity;
import kr.nanoit.domain.message.MessageStatus;
import kr.nanoit.domain.payload.PayloadType;
import lombok.*;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString

public class ClientMessageDto {

    private long id;
    private long agent_id;
    private PayloadType type;
    private MessageStatus status;
    private Timestamp send_time;
    private String sender_num;
    private String sender_callback;
    private String sender_name;
    private String content;
    private Timestamp created_at;
    private Timestamp last_modified_at;

    public ClientMessageEntity toEntity() {
        return new ClientMessageEntity(id, agent_id, type, status, send_time, sender_num, sender_callback, sender_name, content, created_at, last_modified_at);
    }
}
