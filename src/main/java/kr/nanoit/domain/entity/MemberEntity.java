package kr.nanoit.domain.entity;

import kr.nanoit.dto.UserDto;
import lombok.*;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString


public class MemberEntity {
    private long id;

    private String username;

    private String password;

    private String eamil;

    private Timestamp created_at;
    private Timestamp last_modified_at;

    public UserDto toDto() {
        return new UserDto(id, username, password, eamil, created_at, last_modified_at);
    }
}
