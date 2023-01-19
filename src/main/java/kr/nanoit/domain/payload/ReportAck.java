package kr.nanoit.domain.payload;


import kr.nanoit.domain.message.MessageResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportAck {
    private long agent_id;
    private MessageResult result;
}
