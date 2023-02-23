package kr.nanoit.domain.entity;


import kr.nanoit.domain.message.MessageStatus;
import kr.nanoit.domain.payload.PayloadType;
import kr.nanoit.dto.ClientMessageDto;
import lombok.*;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString

public class ClientMessageEntity {

    private long id;
    private long agent_id;
    private PayloadType type;
    private MessageStatus status;
    private long messageNum;
    private Timestamp send_time;
    private String sender_num;
    private String sender_callback;
    private String sender_name;
    private String content;
    private Timestamp created_at;
    private Timestamp last_modified_at;

    public ClientMessageDto toDto() {
        return new ClientMessageDto(id, agent_id, type, status, messageNum, send_time, sender_num, sender_callback, sender_name, content, created_at, last_modified_at);
    }
}
