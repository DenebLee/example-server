package kr.nanoit.domain.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Send {
    private String sender_num;
    private String sender_callback;
    private String sender_name;
    private String content;
}
