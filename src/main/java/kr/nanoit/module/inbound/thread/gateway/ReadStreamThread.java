package kr.nanoit.module.inbound.thread.gateway;


import kr.nanoit.domain.broker.InternalDataMapper;
import kr.nanoit.domain.broker.MetaData;
import kr.nanoit.domain.payload.PayloadType;
import kr.nanoit.module.broker.Broker;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

@Slf4j
public class ReadStreamThread implements Runnable {

    private final Consumer<String> cleaner;
    private final Broker broker;
    private final BufferedReader bufferedReader;
    private final String uuid;
    private boolean isAuth;
    private final Timer timer;


    public ReadStreamThread(Consumer<String> cleaner, Broker broker, BufferedReader bufferedReader, String uuid) {
        this.cleaner = cleaner;
        this.broker = broker;
        this.bufferedReader = bufferedReader;
        this.uuid = uuid;
        this.timer = new Timer();
    }

    @Override
    public void run() {
        log.info("[SERVER : SOCKET : {}] READ START", uuid);
        try {
            int count = 0;
            isAuth = false;


            while (true) {
                if (isAuth == false) {
                    timer.schedule(timerTask, 10000); // 10초
                }
                String payload = bufferedReader.readLine();
                if (payload != null) {
                    if (payload.contains("AUTHENTICATION") && isAuth == false && count == 0) {
                        log.info("[@SOCKET:READ:{}@] Authentication message Receive", uuid);
                        broker.publish(new InternalDataMapper(new MetaData(uuid), payload));
                        isAuth = true;
                    }
                    if (isAuth == true && count > 0) {
                        broker.publish(new InternalDataMapper(new MetaData(uuid), payload));
                    }
                    count++;
                }
            }
        } catch (Throwable e) {
            log.info("[@SOCKET:READ:{}@] terminating...", uuid);
            cleaner.accept(this.getClass().getName());
        }
    }

    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            if (isAuth == false) {
                log.warn("[@SOCKET:READ:{}@] Authentication timeOut", uuid);
                cleaner.accept(this.getClass().getName());
            }
        }
    };
}
