package kr.nanoit.module.inbound.thread;


import kr.nanoit.domain.broker.InternalDataMapper;
import kr.nanoit.domain.broker.MetaData;
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
public class ReadStreamThread implements Runnable {

    private final Consumer<String> cleaner;
    private final Broker broker;
    private final BufferedReader bufferedReader;
    private final String uuid;

    public ReadStreamThread(Consumer<String> cleaner, Broker broker, BufferedReader bufferedReader, String uuid) {
        this.cleaner = cleaner;
        this.broker = broker;
        this.bufferedReader = bufferedReader;
        this.uuid = uuid;
    }


    @Override
    public void run() {
        log.info("[SERVER : SOCKET : {}] READ START", uuid.substring(0, 7));
        try {
            while (true) {
                String readData = bufferedReader.readLine();
//                log.info("[SERVER : SOCKET : {}] READ DATA => [LENGTH = {} PAYLOAD = {}]", uuid.substring(0, 7), readData.length(), readData);
                broker.publish(new InternalDataMapper(new MetaData(uuid), readData));
//                log.info("[SERVER : SOCKET : {}] TO MAPPER => {}", uuid.substring(0, 7), readData);
            }
        } catch (Throwable e) {
            log.info("[@SOCKET:READ:{}@] terminating...", uuid);
            cleaner.accept(this.getClass().getName());
        }
    }
}
