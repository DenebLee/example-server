package kr.nanoit.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InternalDataFilter implements InternalData {

    private MetaData metaData;
    private Message message;

    @Override
    public String UUID() {
        return metaData.getSocketUuid();
    }
}
