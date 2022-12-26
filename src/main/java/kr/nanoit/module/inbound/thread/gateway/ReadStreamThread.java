package kr.nanoit.module.inbound.thread.gateway;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.xml.internal.bind.v2.TODO;
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
    private final String uuid;


    public ReadStreamThread(Consumer<String> cleaner, Broker broker, BufferedReader bufferedReader, String uuid) {
        this.cleaner = cleaner;
        this.broker = broker;
        this.bufferedReader = bufferedReader;
        this.uuid = uuid;
    }

    @Override
    public void run() {
        log.info("[SERVER : SOCKET : {}] READ START", uuid);
        try {
            int count = 0;
            boolean getAuth = false;
            long start = System.currentTimeMillis();

            /*
            타임아웃 변수 만들어서 if 조건에 추가하기 -> ex) 60초 동안 메시지가 전달되지 않으면 바로 cleaner 호출
            */

            while (true) {
                String payload = bufferedReader.readLine();
                // inputStream 은 데이터를 받기 전까지 blocking 상태


                //TODO 해당 로직 대폭 수정해야함

                if (count == 0) {
                    if (payload.contains(PayloadType.AUTHENTICATION.toString())) {
                        //  log.info("[SERVER : SOCKET : {}] READ DATA => [LENGTH = {} PAYLOAD = {}]", uuid.substring(0, 7), readData.length(), readData);
                        broker.publish(new InternalDataMapper(new MetaData(uuid), payload));
                        getAuth = true;
                    } else if (getAuth == false) {
                        log.warn("[@SOCKET:READ:{}@] NOT AUTHENTICATION SEND", uuid);
                        throw new Throwable();
                    }
                } else {
                    log.warn("[@SOCKET:READ:{}@] Timeout to get Authentication", uuid);
                    throw new Throwable();
                }
                //  log.info("[SERVER : SOCKET : {}] READ DATA => [LENGTH = {} PAYLOAD = {}]", uuid.substring(0, 7), readData.length(), readData);
                broker.publish(new InternalDataMapper(new MetaData(uuid), payload));
                //  log.info("[SERVER : SOCKET : {}] TO MAPPER => {}", uuid.substring(0, 7), readData);
                count++;
            }

        } catch (
                Throwable e) {
            log.info("[@SOCKET:READ:{}@] terminating...", uuid);
            cleaner.accept(this.getClass().getName());
        }
    }
}
