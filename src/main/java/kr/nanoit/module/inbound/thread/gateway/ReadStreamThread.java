package kr.nanoit.module.inbound.thread.gateway;


import com.fasterxml.jackson.databind.ObjectMapper;
import kr.nanoit.domain.broker.InternalDataMapper;
import kr.nanoit.domain.broker.MetaData;
import kr.nanoit.domain.payload.Payload;
import kr.nanoit.domain.payload.PayloadType;
import kr.nanoit.module.broker.Broker;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.util.function.Consumer;

@Slf4j
public class ReadStreamThread implements Runnable {

    private final Consumer<String> cleaner;
    private final Broker broker;
    private final BufferedReader bufferedReader;
    private final ObjectMapper objectMapper;
    private final String uuid;

    public ReadStreamThread(Consumer<String> cleaner, Broker broker, BufferedReader bufferedReader, String uuid) {
        this.cleaner = cleaner;
        this.broker = broker;
        this.bufferedReader = bufferedReader;
        this.uuid = uuid;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void run() {
        log.info("[SERVER : SOCKET : {}] READ START", uuid);
        try {
            int count = 0;
            boolean getAuth = false;
            while (true) {
                if (count == 0) {
                    Payload payload = objectMapper.readValue(bufferedReader.readLine(), Payload.class);
                    if (payload.getType() == PayloadType.AUTHENTICATION) {
                        String readData = bufferedReader.readLine();
                        //  log.info("[SERVER : SOCKET : {}] READ DATA => [LENGTH = {} PAYLOAD = {}]", uuid.substring(0, 7), readData.length(), readData);
                        broker.publish(new InternalDataMapper(new MetaData(uuid), readData));
                        getAuth = true;
                    } else if (getAuth == false) {
                        log.warn("NOT AUTHENTICATION SEND");
                        throw new Throwable();
                    }
                }
                String readData = bufferedReader.readLine();
                //  log.info("[SERVER : SOCKET : {}] READ DATA => [LENGTH = {} PAYLOAD = {}]", uuid.substring(0, 7), readData.length(), readData);
                broker.publish(new InternalDataMapper(new MetaData(uuid), readData));
                //  log.info("[SERVER : SOCKET : {}] TO MAPPER => {}", uuid.substring(0, 7), readData);
                count++;
            }
        } catch (Throwable e) {
            log.info("[@SOCKET:READ:{}@] terminating...", uuid);
            cleaner.accept(this.getClass().getName());
        }
    }
}
