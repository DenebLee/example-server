package kr.nanoit.module.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.nanoit.abst.Process;
import kr.nanoit.domain.broker.InternalDataFilter;
import kr.nanoit.domain.broker.InternalDataMapper;
import kr.nanoit.domain.broker.InternalDataType;
import kr.nanoit.domain.payload.Payload;
import kr.nanoit.module.broker.Broker;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;


/**
 * InBound를 내부 규격 혹은 DTO로 변환
 * <p>
 * - 성공 -> 필터로
 * - 실패 -> 아웃바운드로 ( 실패 메시지를 Client 로 전송 해야됨 )
 */
@Slf4j
public class Mapper implements Process {
    private final Broker broker;
    private final ObjectMapper objectMapper;

    public Mapper(Broker broker) {
        this.objectMapper = new ObjectMapper();
        this.broker = broker;
    }

    @Override
    public void run() {
        while (true) {
            Object object;
            try {
                object = broker.subscribe(InternalDataType.MAPPER);
                if (object != null && object instanceof InternalDataMapper) {
//                    log.info("[MAPPER]   READ-THREAD DATA INPUT => [{}]", object);
                    InternalDataMapper internalDataMapper = (InternalDataMapper) object;
                    Payload payload = objectMapper.readValue(internalDataMapper.getPayload(), Payload.class);

                    if (broker.publish(new InternalDataFilter(internalDataMapper.getMetaData(), payload))) {
//                        log.info("[MAPPER]   TO FILTER => [TYPE : {} DATA : {}]", payload.getType(), payload.getData());
                    } else {
                        log.error("[MAPPER]   NOT FOUND DATA => [{}]", payload);
                    }
                }

            } catch (InterruptedException | JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public String getUuid() {
        return UUID.randomUUID().toString().substring(0, 7);
    }
}
