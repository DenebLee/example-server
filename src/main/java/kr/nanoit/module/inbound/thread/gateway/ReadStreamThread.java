package kr.nanoit.module.inbound.thread.gateway;


import kr.nanoit.db.auth.AuthenticaionStatus;
import kr.nanoit.domain.broker.InternalDataMapper;
import kr.nanoit.domain.broker.MetaData;
import kr.nanoit.module.broker.Broker;
import kr.nanoit.module.inbound.socket.UserManager;
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
    private boolean isAuth;
    private final AtomicBoolean test;
    private final UserManager userManager;


    public ReadStreamThread(Consumer<String> cleaner, Broker broker, BufferedReader bufferedReader, String uuid, AtomicBoolean test, UserManager userManager) {
        this.cleaner = cleaner;
        this.broker = broker;
        this.bufferedReader = bufferedReader;
        this.uuid = uuid;
        this.test = test;
        this.userManager = userManager;
    }

    @Override
    public void run() {
        log.info("[SERVER : SOCKET : {}] READ START", uuid);
        try {
            int count = 0;
            long startTime = System.currentTimeMillis();
            isAuth = false;

            while (true) {
                if (isAuth == false && (System.currentTimeMillis() - startTime) / 1000 == 10) { // 10초
                    throw new Exception("Authentication Timeout");
                }
                String payload = bufferedReader.readLine();
                if (payload != null) {
                    if (payload.contains("AUTHENTICATION") && isAuth == false && count == 0) {
                        log.info("[@SOCKET:READ:{}@] Authentication message Receive", uuid);
                        broker.publish(new InternalDataMapper(new MetaData(uuid), payload));
                        isAuth = true;
                        if (userManager.registUser(uuid, AuthenticaionStatus.BEFORE)) {
                            System.out.println("Usermanger에 등록완료");
                        }
                    }
                    if (isAuth == true && count > 0) {
                        broker.publish(new InternalDataMapper(new MetaData(uuid), payload));
                    }
                    if (isAuth == true && test.get() == true) {
                        count++;
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
            log.info("[@SOCKET:READ:{}@] terminating...", uuid);
            cleaner.accept(this.getClass().getName());
        }
    }
}


// timertask를 재 사용할 수 없다. 새 인스턴스를 만들던지 객체를 새로 생성해야됨
//                    Timer timer = new Timer();
//
//                    TimerTask timerTask = new TimerTask() {
//                        @Override
//                        public void run() {
//                            if (isAuth == false) {
//                                log.warn("[@SOCKET:READ:{}@] Authentication timeOut", uuid);
//                                cleaner.accept(this.getClass().getName());
//                                cancel();
//                            }
//                        }
//                    };
//                    timer.schedule(timerTask, 5000);
