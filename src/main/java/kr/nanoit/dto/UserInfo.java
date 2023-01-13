package kr.nanoit.dto;

import kr.nanoit.db.auth.AuthenticaionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)

public class UserInfo {
    private String username;
    private long MemberId;
    private long agent_id;
    private AuthenticaionStatus authenticaionStatus;
}
