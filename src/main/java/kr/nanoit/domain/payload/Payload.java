package kr.nanoit.domain.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payload {
    private PayloadType type;
    private String messageUuid;
    private Object data;
}