package kr.nanoit.module.borker;

import kr.nanoit.domain.broker.InternalData;
import kr.nanoit.domain.broker.InternalDataFilter;
import kr.nanoit.domain.broker.InternalDataMapper;
import kr.nanoit.domain.broker.InternalDataType;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
public class BrokerImpl implements Broker {

    private final Map<InternalDataType, LinkedBlockingQueue<Object>> brokerQueue;

    public BrokerImpl() {
        this.brokerQueue = new HashMap<>();
        for (InternalDataType value : InternalDataType.values()) {
            brokerQueue.put(value, new LinkedBlockingQueue<>());
        }
    }

    @Override
    public boolean publish(Object object) {
        if (object instanceof InternalData) {
            log.info("[BROKER:PUBLISH:{}] input", ((InternalData) object).UUID());
            if (object instanceof InternalDataMapper) {
                brokerQueue.get(InternalDataType.MAPPER).offer(object); // BLOCKING 가능성
                return true;
            } else if (object instanceof InternalDataFilter) {
                brokerQueue.get(InternalDataType.FILTER).offer(object);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public Object subscribe(InternalDataType type) throws InterruptedException {
        log.info("[BROKER:SUBSCRIBE] type={} ", type);
        return brokerQueue.get(type).poll(1, TimeUnit.SECONDS);
    }

    @Override
    public Object outBound(InternalDataType type, String uuid) {
        return null;
    }
}
