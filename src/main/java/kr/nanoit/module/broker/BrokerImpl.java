package kr.nanoit.module.broker;

import kr.nanoit.domain.broker.*;
import kr.nanoit.module.inbound.socket.SocketManager;
import kr.nanoit.module.inbound.socket.SocketResource;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
public class BrokerImpl implements Broker {

    private final Map<InternalDataType, LinkedBlockingQueue<Object>> brokerQueue;
    private final SocketManager socketManager;

    public BrokerImpl(SocketManager socketManager) {
        this.brokerQueue = new HashMap<>();
        this.socketManager = socketManager;

        for (InternalDataType value : InternalDataType.values()) {
            brokerQueue.put(value, new LinkedBlockingQueue<>());
        }
    }

    @Override
    public boolean publish(Object object) {
        if (object != null) {
            if (object instanceof InternalData) {
                if (object instanceof InternalDataMapper) {
//                    log.info("[BROKER : PUBLISH : {}] To MAPPER => {}", ((InternalData) object).UUID().substring(0, 7), object);
                    if (brokerQueue.get(InternalDataType.MAPPER).offer(object)) {
                        return true;
                    } else {
                        return false;
                    }
                } else if (object instanceof InternalDataFilter) {
//                    log.info("[BROKER : PUBLISH : {}] To FILTER => {}", ((InternalData) object).UUID().substring(0, 7), object);
                    if (brokerQueue.get(InternalDataType.FILTER).offer(object)) {
                        return true;
                    } else {
                        return false;
                    }
                } else if (object instanceof InternalDataBranch) {
//                    log.info("[BROKER : PUBLISH : {}] TO BRANCH => {}", ((InternalData) object).UUID().substring(0, 7), object);
                    if (brokerQueue.get(InternalDataType.BRANCH).offer(object)) {
                        return true;
                    } else {
                        return false;
                    }
                } else if (object instanceof InternalDataSender) {
//                    log.info("[BROKER : PUBLISH : {}] TO SENDER => {}", ((InternalData) object).UUID().substring(0, 7), object);
                    if (brokerQueue.get(InternalDataType.SENDER).offer(object)) {
                        return true;
                    } else {
                        return false;
                    }
                } else if (object instanceof InternalDataOutBound) {
//                    log.info("[BROKER : PUBLISH : {}] to OUTBOUND => {}", ((InternalData) object).UUID().substring(0, 7), object);
                    if (brokerQueue.get(InternalDataType.OUTBOUND).offer(object)) {
                        return true;
                    } else {
                        return false;
                    }
                } else if (object instanceof InternalDataCarrier) {
                    if (brokerQueue.get(InternalDataType.CARRIER).offer(object)) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public Object subscribe(InternalDataType type) throws InterruptedException {
        return brokerQueue.get(type).poll(1, TimeUnit.SECONDS);
    }

    @Override
    public boolean outBound(String uuid, String payload) {
        if (uuid == null || uuid == "" || uuid.contains(" ")) {
            System.out.println("uuid 오류");
            return false;
        }
        if (payload == null || payload == "") {
            System.out.println("payload 오류");
            return false;
        }

        SocketResource socketResource = socketManager.getSocketResource(uuid);
        socketResource.write(payload);
        return true;
    }


    @Override
    public int getBrokerMapSize() {
        return brokerQueue.size();
    }

    @Override
    public int getInternalDataInBrokerMap(InternalDataType internalDataType) {
        return brokerQueue.get(internalDataType).size();
    }

    @Override
    public int getOutBoundQueueSize(String uuid) {
        SocketResource socketResource = socketManager.getSocketResource(uuid);
        return socketResource.getWriteBufferQueueSize();
    }

}
