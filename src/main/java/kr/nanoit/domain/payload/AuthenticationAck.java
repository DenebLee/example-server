package kr.nanoit.domain.payload;

import kr.nanoit.domain.message.MessageResult;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AuthenticationAck {

    private long agent_id;
    private MessageResult result;
}
