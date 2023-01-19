package kr.nanoit.module.inbound.thread.gateway;


import kr.nanoit.domain.broker.InternalDataMapper;
import kr.nanoit.domain.broker.MetaData;
import kr.nanoit.module.broker.Broker;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@Slf4j
public class ReadStreamThread implements Runnable {

    private final Consumer<String> cleaner;
    private final Broker broker;
    private final BufferedReader bufferedReader;
    private final String uuid;
    private AtomicBoolean readThreadStatus;
    private boolean isAuth;


    public ReadStreamThread(Consumer<String> cleaner, Broker broker, BufferedReader bufferedReader, String uuid, AtomicBoolean readThreadStatus) {
        this.cleaner = cleaner;
        this.broker = broker;
        this.bufferedReader = bufferedReader;
        this.uuid = uuid;
        this.readThreadStatus = readThreadStatus;
    }

    @Override
    public void run() {
        log.info("[SERVER : SOCKET : {}] READ START", uuid);
        try {
            long startTime = System.currentTimeMillis();
            isAuth = false;

            while (readThreadStatus.get()) {
                if (isAuth == false && (System.currentTimeMillis() - startTime) / 1000 == 5) {
                    throw new Exception("Authentication Timeout");
                }

                String payload = bufferedReader.readLine();
                if (payload != null) {
                    if (payload.contains("AUTHENTICATION")) {
                        isAuth = true;
                    }
                    broker.publish(new InternalDataMapper(new MetaData(uuid), payload));
                    log.debug("[@SOCKET:READ:{}@] Receive DATA ", uuid);
                }
            }
        } catch (
                Exception e) {
            log.warn("[@SOCKET:READ:{}@] terminating...", uuid, e);
            cleaner.accept(this.getClass().getName());
        }
        log.info("[@SOCKET:READ:{}@] Close Success", uuid);
    }
}
