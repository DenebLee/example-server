package kr.nanoit.module.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.nanoit.domain.broker.InternalDataFilter;
import kr.nanoit.domain.broker.InternalDataMapper;
import kr.nanoit.domain.broker.InternalDataType;
import kr.nanoit.domain.payload.Authentication;
import kr.nanoit.domain.payload.Payload;
import kr.nanoit.module.borker.Broker;
import lombok.extern.slf4j.Slf4j;


/**
 * InBound를 내부 규격 혹은 DTO로 변환
 * <p>
 * - 성공 -> 필터로
 * - 실패 -> 아웃바운드로 ( 실패 메시지를 Client 로 전송 해야됨 )
 */
@Slf4j
public class Mapper implements Runnable {
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
            Payload payload;
            // 맵핑 후 바로 filter로 넘김
            try {
                object = broker.subscribe(InternalDataType.MAPPER);
                if (object instanceof InternalDataMapper) {
                    InternalDataMapper internalDataMapper = (InternalDataMapper) object;
                    payload = objectMapper.readValue(internalDataMapper.getPayload(), Payload.class);
                    // TODO json mapper 처리
                    broker.publish(new InternalDataFilter(internalDataMapper.getMetaData(), payload));
                    log.info("Mapper send to Filter [Type : {} data : {}]", internalDataMapper.getMetaData(), payload.getData());
                } else {
                    log.error(" NOT FOUND : {}", object);
                }
            } catch (InterruptedException | JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
