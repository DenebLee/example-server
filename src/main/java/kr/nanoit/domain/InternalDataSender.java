package kr.nanoit.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InternalDataSender implements InternalData {

    @Override
    public String UUID() {
        return null;
    }
}
