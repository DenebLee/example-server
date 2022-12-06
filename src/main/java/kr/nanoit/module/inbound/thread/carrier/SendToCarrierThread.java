package kr.nanoit.module.inbound.thread.carrier;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.nanoit.domain.broker.InternalDataCarrier;
import kr.nanoit.domain.broker.InternalDataType;
import kr.nanoit.extension.Jackson;
import kr.nanoit.module.broker.Broker;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
public class SendToCarrierThread implements Runnable {

    private final Consumer<String> cleaner;
    private final DataOutputStream dataOutputStream;
    private final String uuid;
    private final Broker broker;
    private final ObjectMapper objectMapper;

    public SendToCarrierThread(Consumer<String> cleaner, Broker broker, DataOutputStream dataOutputStream, String uuid) {
        this.cleaner = cleaner;
        this.dataOutputStream = dataOutputStream;
        this.uuid = uuid;
        this.broker = broker;
        this.objectMapper = Jackson.getInstance().getObjectMapper();
    }


    @Override
    public void run() {
        log.info("[SERVER : SOCKET : {}] CARRIER WRITE START", uuid);
        try {
            while (true) { // BUSY WAITING : 리소스
                Object object = broker.subscribe(InternalDataType.CARRIER);
                if (object != null && object instanceof InternalDataCarrier) {
                    InternalDataCarrier internalDataCarrier = (InternalDataCarrier) object;
                    if (internalDataCarrier.getMetaData() != null && internalDataCarrier.getPayload().getData() != null) {
                        String sendToCarrierData = objectMapper.writeValueAsString(internalDataCarrier) + "\r\n";
                        dataOutputStream.write(sendToCarrierData.getBytes(StandardCharsets.UTF_8));
                    }
                }
            }
        } catch (Throwable e) {
            log.info("[@SOCKET:RECEIVE-CARRIER:{}@] terminating...", uuid);
            cleaner.accept(this.getClass().getName());
            e.printStackTrace();
        }
    }

}
