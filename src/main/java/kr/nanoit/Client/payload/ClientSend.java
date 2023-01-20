package kr.nanoit.Client.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)

public class ClientSend {
    private long agent_id;
    private String sender_num;
    private String sender_callback;
    private String sender_name;
    private String content;
}
