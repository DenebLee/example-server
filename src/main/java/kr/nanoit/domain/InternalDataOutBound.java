package kr.nanoit.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class InternalDataOutBound implements InternalData {

    @Override
    public String UUID() {
        return null;
    }
}
