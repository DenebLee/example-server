package kr.nanoit.domain.entity;


import kr.nanoit.domain.message.MessageStatus;
import kr.nanoit.domain.payload.PayloadType;
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
    private Timestamp send_time;
    private String sender_num;
    private String sender_callback;
    private String sender_name;
    private Timestamp receive_time;
    private String content;
    private Timestamp created_at;
    private Timestamp last_modified_at;
}
