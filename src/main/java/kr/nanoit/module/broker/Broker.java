package kr.nanoit.module.broker;

import kr.nanoit.domain.broker.InternalDataType;

public interface Broker {
    boolean publish(Object payload);

    Object subscribe(InternalDataType type) throws InterruptedException;

    boolean outBound(String uuid, String payload);

    int getBrokerMapSize();

    int getInternalDataInBrokerMap(InternalDataType internalDataType);


}
