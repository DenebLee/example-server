package kr.nanoit.module.inbound.thread.gateway;

import kr.nanoit.db.auth.AuthenticaionStatus;
import kr.nanoit.module.inbound.socket.UserManager;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

@Slf4j
public class WriteStreamThread implements Runnable {

    private final Consumer<String> cleaner;
    private final BufferedWriter bufferedWriter;
    private final String uuid;
    private final LinkedBlockingQueue<String> writeBuffer;
    private String payload;
    private final AtomicBoolean authSate;

    public WriteStreamThread(Consumer<String> cleaner, BufferedWriter bufferedWriter, String uuid, LinkedBlockingQueue<String> writeBuffer, AtomicBoolean authSate) {
        this.cleaner = cleaner;
        this.bufferedWriter = bufferedWriter;
        this.uuid = uuid;
        this.writeBuffer = writeBuffer;
        this.authSate = authSate;
    }


    @Override
    public void run() {
        log.info("[SERVER : SOCKET : {}] WRITE START", uuid.substring(0, 7));
        try {
            while (true) { // BUSY WAITING : 리소스
                payload = writeBuffer.poll(1, TimeUnit.SECONDS);
                if (payload != null) {
                    if (authSate.get() == false) {
                        send(payload);
                        throw new Throwable();
                    }
                    if (send(payload)) {
                        log.info("[SERVER : SOCKET : {}] WRITE SUCCESS! => Payload : {}", uuid.substring(0, 7), payload);
                    }
                }
            }
        } catch (Throwable e) {
            log.info("[@SOCKET:READ:{}@] terminating...", uuid);
            cleaner.accept(this.getClass().getName());
        }
    }

    private boolean send(String data) throws IOException {
        bufferedWriter.write(data + "\r\n");
        bufferedWriter.flush();
        return true;
    }
}