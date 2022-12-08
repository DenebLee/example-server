package kr.nanoit.old;

import kr.nanoit.domain.broker.InternalDataType;
import kr.nanoit.module.broker.Broker;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class MapperTest {

    @Test
    void name() throws InterruptedException {
        Broker broker = spy(Broker.class);
        when(broker.subscribe(InternalDataType.MAPPER)).thenThrow(new RuntimeException());
        when(broker.publish(any())).thenThrow(new RuntimeException());

        Mapper mapper = new Mapper(broker);
        mapper.run(); //block

//        mapper.status();
    }
}