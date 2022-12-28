package kr.nanoit.domain.VO;


import kr.nanoit.db.auth.AuthenticaionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor

public class UserVO {
    private String username;
    private long agent_id;
    private AuthenticaionStatus status;
}
