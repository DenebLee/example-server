package kr.nanoit.Client.payload;

import kr.nanoit.domain.message.MessageResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientSendAck {
    private MessageResult result;
}
