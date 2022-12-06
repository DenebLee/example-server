package kr.nanoit.module.inbound.thread.carrier;


import com.fasterxml.jackson.databind.ObjectMapper;
import kr.nanoit.domain.broker.InternalDataMapper;
import kr.nanoit.domain.broker.InternalDataOutBound;
import kr.nanoit.domain.broker.MetaData;
import kr.nanoit.extension.Jackson;
import kr.nanoit.module.broker.Broker;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.util.function.Consumer;

/**
 * 검색 해볼것.
 * - busy waiting
 * - blocking
 */
@Slf4j
public class ReceiveFromCarrierThread implements Runnable {

    private final Consumer<String> cleaner;
    private final Broker broker;
    private final BufferedReader bufferedReader;
    private final String uuid;
    private final ObjectMapper objectMapper;

    public ReceiveFromCarrierThread(Consumer<String> cleaner, Broker broker, BufferedReader bufferedReader, String uuid) {
        this.cleaner = cleaner;
        this.broker = broker;
        this.bufferedReader = bufferedReader;
        this.uuid = uuid;
        this.objectMapper = Jackson.getInstance().getObjectMapper();
    }


    @Override
    public void run() {
        log.info("[SERVER : SOCKET : {}] CARRIER RECEIVE START", uuid);
        try {
            while (true) {
                String readData = bufferedReader.readLine();

                InternalDataOutBound internalDataOutBound = objectMapper.readValue(readData, InternalDataOutBound.class);
                if (internalDataOutBound != null) {
                    broker.publish(internalDataOutBound);
                }
            }
        } catch (Throwable e) {
            log.info("[@SOCKET:CARRIER-READ:{}@] terminating...", uuid);
            cleaner.accept(this.getClass().getName());
            e.printStackTrace();
        }
    }
}
