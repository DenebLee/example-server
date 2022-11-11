package kr.nanoit.module.borker;

import kr.nanoit.domain.broker.InternalDataType;

public interface Broker {
    boolean publish(Object object);

    Object subscribe(InternalDataType type) throws InterruptedException;

    Object outBound(InternalDataType type, String uuid);
}
