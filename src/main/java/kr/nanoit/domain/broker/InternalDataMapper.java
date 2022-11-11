package kr.nanoit.domain.broker;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InternalDataMapper implements InternalData {
    private MetaData metaData;
    private String payload;

    @Override
    public String UUID() {
        return metaData.getSocketUuid();
    }
}
