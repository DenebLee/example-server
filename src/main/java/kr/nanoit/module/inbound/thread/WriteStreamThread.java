package kr.nanoit.module.inbound.thread;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
public class WriteStreamThread implements Runnable {

    private final Consumer<String> cleaner;
    private final BufferedWriter bufferedWriter;
    private final String uuid;
    private final LinkedBlockingQueue<String> writeBuffer;

    public WriteStreamThread(Consumer<String> cleaner, BufferedWriter bufferedWriter, String uuid, LinkedBlockingQueue<String> writeBuffer) {
        this.cleaner = cleaner;
        this.bufferedWriter = bufferedWriter;
        this.uuid = uuid;
        this.writeBuffer = writeBuffer;
    }


    @Override
    public void run() { // THREAD run을 실행 하는데 지금 while 무한루프
        log.info("[SERVER : SOCKET : {}] WRITE START", uuid.substring(0, 7));
        try {
            while (true) { // BUSY WAITING : 리소스 낭비가 제일 심한 로직
                log.info("작동 테스트");
                String payload = writeBuffer.poll(1, TimeUnit.SECONDS); // BLOCKING 유도해서 1초 안에 데이터가 있으면 가져오고 없으면 1초 후 로직 실행
                if (payload != null) {
                    if (send(payload)) {
                        log.info("[SERVER : SOCKET : {}] WRITE SUCCESS! => Payload : {}", uuid.substring(0, 7), payload);
                    }
                }
            }
        } catch (Throwable e) {
            log.info("[@SOCKET:READ:{}@] terminating...", uuid, e);
            cleaner.accept(this.getClass().getName());
        }
    }

    private boolean send(String data) throws IOException {
        bufferedWriter.write(data + "\r\n");
        bufferedWriter.flush();
        return true;
    }
}
