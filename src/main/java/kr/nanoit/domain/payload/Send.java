package kr.nanoit.domain.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Send {
    private long messageNum;
    private String phoneNum;
    private String callback;
    private String name;
    private String content;
}
