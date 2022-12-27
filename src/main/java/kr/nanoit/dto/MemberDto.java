package kr.nanoit.dto;

import kr.nanoit.domain.entity.MemberEntity;
import lombok.*;
import lombok.experimental.Accessors;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Accessors(chain = true)

public class MemberDto {
    private long id;
    private String username;
    private String password;
    private String email;
    private Timestamp created_at;
    private Timestamp last_modified_at;

    public MemberEntity toEntity() {
        return new MemberEntity(id, username, password, email, created_at, last_modified_at);
    }

}
