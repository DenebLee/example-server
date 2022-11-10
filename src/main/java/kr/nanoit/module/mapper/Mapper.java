package kr.nanoit.module.mapper;

import kr.nanoit.domain.InternalDataFilter;
import kr.nanoit.domain.InternalDataMapper;
import kr.nanoit.domain.InternalDataType;
import kr.nanoit.domain.Message;
import kr.nanoit.module.borker.Broker;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Mapper implements Runnable {
    private final Broker broker;

    public Mapper(Broker broker) {
        this.broker = broker;
    }

    @Override
    public void run() {
        while (true) {
            Object object = null;
            try {
                object = broker.subscribe(InternalDataType.MAPPER);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (object == null) {
                continue;
            }

            if (object != null && object instanceof InternalDataMapper) {
                InternalDataMapper internalDataMapper = (InternalDataMapper) object;
                // TODO json mapper 처리
                log.info("[MAPPER:POLL:{}] payload={}", internalDataMapper.UUID(), internalDataMapper.getPayload());

                broker.publish(new InternalDataFilter(internalDataMapper.getMetaData(), new Message()));
            } else {
                log.error(" NOT FOUND{}", object);
            }
        }
    }
}
