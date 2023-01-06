package kr.nanoit.dto;

import kr.nanoit.domain.entity.CompanyMessageEntity;
import kr.nanoit.domain.message.MessageStatus;
import kr.nanoit.domain.payload.PayloadType;
import lombok.*;
import lombok.experimental.Accessors;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Accessors(chain = true)


public class CompanyMessageDto {
    private long client_message_id;
    private long relay_company_id;
    private PayloadType type;
    private MessageStatus status;

    private Timestamp send_time;
    private String sender_num;
    private String sender_callback;
    private String sender_name;
    private String content;
    private Timestamp created_at;
    private Timestamp last_modified_at;

    public CompanyMessageEntity toEntity() {
        return new CompanyMessageEntity(client_message_id, relay_company_id, type, status, send_time, sender_num, sender_callback, sender_name, content, created_at, last_modified_at);
    }
}
