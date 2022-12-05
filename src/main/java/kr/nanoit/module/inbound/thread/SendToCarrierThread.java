package kr.nanoit.module.inbound.thread;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
public class SendToCarrierThread implements Runnable {

    private final Consumer<String> cleaner;
    private final DataOutputStream dataOutputStream;
    private final String uuid;
    private String payload;

    public SendToCarrierThread(Consumer<String> cleaner, DataOutputStream dataOutputStream, String uuid) {
        this.cleaner = cleaner;
        this.dataOutputStream = dataOutputStream;
        this.uuid = uuid;
    }


    @Override
    public void run() {
        log.info("[SERVER : SOCKET : {}] WRITE START", uuid.substring(0, 7));
        try {
            while (true) { // BUSY WAITING : 리소스

            }
        } catch (Throwable e) {
            log.info("[@SOCKET:READ:{}@] terminating...", uuid);
            cleaner.accept(this.getClass().getName());
        }
    }

}
