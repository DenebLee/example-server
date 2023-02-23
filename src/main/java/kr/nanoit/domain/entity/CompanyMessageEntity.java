package kr.nanoit.domain.entity;


import kr.nanoit.domain.message.MessageStatus;
import kr.nanoit.domain.payload.PayloadType;
import kr.nanoit.dto.CompanyMessageDto;
import lombok.*;
import lombok.experimental.Accessors;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString


public class CompanyMessageEntity {

    // Foreign key
    private long client_message_id;
    private long relay_company_id;
    private PayloadType type;
    private MessageStatus status;
    private Timestamp send_time;
    private long message_num;
    private String sender_num;
    private String sender_callback;
    private String sender_name;
    private String content;
    private Timestamp created_at;
    private Timestamp last_modified_at;

    public CompanyMessageDto toDto() {
        return new CompanyMessageDto(client_message_id, relay_company_id, type, status, send_time, message_num, sender_num, sender_callback, sender_name, content, created_at, last_modified_at);
    }
}
