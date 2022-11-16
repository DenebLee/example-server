package kr.nanoit.domain.broker;

import kr.nanoit.domain.payload.Payload;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InternalDataSender implements InternalData {

    private MetaData metaData;
    private Payload payload;

    @Override
    public String UUID() {
        return metaData.getSocketUuid();
    }
}
