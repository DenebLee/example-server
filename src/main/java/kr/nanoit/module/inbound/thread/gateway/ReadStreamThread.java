package kr.nanoit.module.inbound.thread.gateway;


import kr.nanoit.db.auth.AuthenticaionStatus;
import kr.nanoit.domain.broker.InternalDataMapper;
import kr.nanoit.domain.broker.InternalDataOutBound;
import kr.nanoit.domain.broker.MetaData;
import kr.nanoit.domain.payload.Payload;
import kr.nanoit.domain.payload.PayloadType;
import kr.nanoit.dto.UserInfo;
import kr.nanoit.module.broker.Broker;
import kr.nanoit.module.inbound.socket.UserManager;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@Slf4j
public class ReadStreamThread implements Runnable {

    private final Consumer<String> cleaner;
    private final Broker broker;
    private final BufferedReader bufferedReader;
    private final String uuid;
    private AtomicBoolean readThreadStatus;


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
            while (readThreadStatus.get()) {
                String payload = bufferedReader.readLine();
                if (payload != null) {
                    broker.publish(new InternalDataMapper(new MetaData(uuid), payload));
                    log.debug("[@SOCKET:READ:{}@] Receive DATA ", uuid);
                }
            }
        } catch (Exception e) {
            log.error("[@SOCKET:READ:{}@] terminating...", uuid, e);
            cleaner.accept(this.getClass().getName());
        }

        log.info("[@SOCKET:READ:{}@] Close Success", uuid);
    }
}
