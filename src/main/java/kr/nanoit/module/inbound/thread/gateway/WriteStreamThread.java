package kr.nanoit.module.inbound.thread.gateway;

import kr.nanoit.db.auth.AuthenticaionStatus;
import kr.nanoit.module.inbound.socket.UserManager;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
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
    private final UserManager userManager;
    private AtomicBoolean writeThreadStatus;


    public WriteStreamThread(Consumer<String> cleaner, BufferedWriter bufferedWriter, String uuid, LinkedBlockingQueue<String> writeBuffer, UserManager userManager, AtomicBoolean writeThreadStatus) {
        this.cleaner = cleaner;
        this.bufferedWriter = bufferedWriter;
        this.uuid = uuid;
        this.writeBuffer = writeBuffer;
        this.userManager = userManager;
        this.writeThreadStatus = writeThreadStatus;
    }


    @Override
    public void run() {
        log.info("[SERVER : SOCKET : {}] WRITE START", uuid);
        try {
            while (writeThreadStatus.get()) {
                String payload = writeBuffer.poll(1, TimeUnit.SECONDS);

                if (payload != null) {
                    if (userManager.getUserInfo(uuid).getAuthenticaionStatus() == AuthenticaionStatus.FAILED) {
                        if (send(payload)) {
                            log.info("[SERVER : SOCKET : {}] Authenticaion Failed Send SUCCESS! => Payload : {}", uuid, payload);
                            throw new Exception();
                        }
                    } else if (userManager.getUserInfo(uuid).getAuthenticaionStatus() == AuthenticaionStatus.COMPLETE) {
                        if (send(payload)) {
                            log.info("[SERVER : SOCKET : {}] WRITE SUCCESS! => Payload : {}", uuid, payload);
                        }
                    } else {
                        log.info("discard {}", payload);
                    }
                } else {
                    log.error("[@SOCKET:WRITE:{}@] Payload Data null", uuid);
                }
            }
        } catch (Exception e) {
            log.error("[@SOCKET:WRITE:{}@] terminating...", uuid, e);
            cleaner.accept(this.getClass().getName());
        }

        log.info("[@SOCKET:WRITE:{}@] Close Success", uuid);
    }

    private boolean send(String data) throws IOException {
        bufferedWriter.write(data + "\r\n");
        bufferedWriter.flush();
        return true;
    }
}