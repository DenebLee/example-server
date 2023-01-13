package kr.nanoit.module.inbound.thread.gateway;


import kr.nanoit.db.auth.AuthenticaionStatus;
import kr.nanoit.domain.broker.InternalDataMapper;
import kr.nanoit.domain.broker.MetaData;
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
    private boolean isAuth;
    private final UserManager userManager;
    private AtomicBoolean readThreadStatus;


    public ReadStreamThread(Consumer<String> cleaner, Broker broker, BufferedReader bufferedReader, String uuid, UserManager userManager, AtomicBoolean readThreadStatus) {
        this.cleaner = cleaner;
        this.broker = broker;
        this.bufferedReader = bufferedReader;
        this.uuid = uuid;
        this.userManager = userManager;
        this.readThreadStatus = readThreadStatus;
    }

    @Override
    public void run() {
        log.info("[SERVER : SOCKET : {}] READ START", uuid);
        try {
            int count = 0;
            long startTime = System.currentTimeMillis();
            isAuth = false;

            while (readThreadStatus.get()) {
                if (isAuth == false && (System.currentTimeMillis() - startTime) / 1000 == 5) { // 5초
                    throw new Exception("Authentication Timeout");
                }

                String payload = bufferedReader.readLine();
                if (payload.contains("AUTHENTICATION") && isAuth == false && count == 0) {

                    log.info("[@SOCKET:READ:{}@] Authentication message Receive", uuid);
                    broker.publish(new InternalDataMapper(new MetaData(uuid), payload));
                    isAuth = true;

                    UserInfo userInfo = new UserInfo();
                    // 우선적으로 AuthenticationStatus에만 값을 넣는다
                    userInfo.setAuthenticaionStatus(AuthenticaionStatus.BEFORE);

                    if (userManager.registUser(uuid, userInfo)) {
                        log.info("[@SOCKET:READ:{}@] Usermanager register Success", uuid);
                    }
                }

                if (isAuth == true && count > 0) {
                    broker.publish(new InternalDataMapper(new MetaData(uuid), payload));
                }

                if (isAuth == true && userManager.getUserInfo(uuid).getAuthenticaionStatus() == AuthenticaionStatus.COMPLETE) {
                    count++;
                    System.out.println("카운트  증가");
                }
//                } else {
//                    log.error("[@SOCKET:READ:{}@] Payload Data null", uuid);
//                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("왜 여기서 오류나는건데");
        } catch (Exception e) {
            log.error("[@SOCKET:READ:{}@] terminating...", uuid, e);
            cleaner.accept(this.getClass().getName());
        }

        log.info("[@SOCKET:READ:{}@] Close Success", uuid);
    }
}
