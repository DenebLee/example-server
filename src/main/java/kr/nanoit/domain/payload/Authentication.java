package kr.nanoit.domain.payload;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Authentication {
    private String username;
    private String password;
}
