package kr.nanoit.domain.payload;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Authentication {
    private long agent_id;
    private String username;
    private String password;
    private String email;
}
