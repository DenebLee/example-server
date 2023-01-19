package kr.nanoit.module.inbound.thread.gateway;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@Slf4j
public class WriteStreamThread implements Runnable {

    private final Consumer<String> cleaner;
    private final String uuid;
    private final LinkedBlockingQueue<String> writeBuffer;
    private AtomicBoolean writeThreadStatus;
    private BufferedWriter bufferedWriter;


    public WriteStreamThread(Consumer<String> cleaner, BufferedWriter bufferedWriter, String uuid, LinkedBlockingQueue<String> writeBuffer, AtomicBoolean writeThreadStatus) {
        this.cleaner = cleaner;
        this.bufferedWriter = bufferedWriter;
        this.uuid = uuid;
        this.writeBuffer = writeBuffer;
        this.writeThreadStatus = writeThreadStatus;
    }


    @Override
    public void run() {
        log.info("[SERVER : SOCKET : {}] WRITE START", uuid);
        try {
            while (writeThreadStatus.get()) {
                String payload = writeBuffer.poll(1, TimeUnit.SECONDS);
                if (payload != null) {
                    if (send(payload)) {
                        log.info("[SERVER : SOCKET : {}] WRITE SUCCESS !! => Payload : {}", uuid, payload);
                    } else {
                        log.error("[@SOCKET:WRITE:{}@] Send Error", uuid);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("[@SOCKET:WRITE:{}@] terminating...", uuid, e);
            cleaner.accept(this.getClass().getName());
        }
        log.info("[@SOCKET:WRITE:{}@] Close Success", uuid);
    }

    private boolean send(String data) throws IOException {
        data = data + "\n";
        bufferedWriter.write(data);
        bufferedWriter.flush();
        return true;
    }
}