package kr.nanoit.domain.broker;

import kr.nanoit.domain.payload.ErrorType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InternalDataOutBound implements InternalData {

    private ErrorType errorType;
    private String errorContent;

    @Override
    public String UUID() {
        return null;
    }
}
