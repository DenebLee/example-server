package kr.nanoit.Client.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)

public class ClientPayload {
    private ClientPayloadType type;
    private int messageUuid;
    private Object data;
}