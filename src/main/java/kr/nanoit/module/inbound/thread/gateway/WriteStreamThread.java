package kr.nanoit.module.inbound.thread.gateway;

import kr.nanoit.db.auth.AuthenticaionStatus;
import kr.nanoit.module.inbound.socket.UserManager;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@Slf4j
public class WriteStreamThread implements Runnable {

    private final Consumer<String> cleaner;
    //    private final BufferedWriter bufferedWriter;
    private final String uuid;
    private final LinkedBlockingQueue<String> writeBuffer;
    private AtomicBoolean writeThreadStatus;
    private DataOutputStream dataOutputStream;


    public WriteStreamThread(Consumer<String> cleaner, DataOutputStream dataOutputStream, String uuid, LinkedBlockingQueue<String> writeBuffer, AtomicBoolean writeThreadStatus) {
        this.cleaner = cleaner;
        this.dataOutputStream = dataOutputStream;
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
                        log.info("[SERVER : SOCKET : {}] WRITE SUCCESS! => Payload : {}", uuid, payload);
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
        byte[] paylaod = data.getBytes(StandardCharsets.UTF_8);
        dataOutputStream.write(paylaod);
        dataOutputStream.flush();
        return true;
    }
}