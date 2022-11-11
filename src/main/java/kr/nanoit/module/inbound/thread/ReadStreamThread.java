package kr.nanoit.module.inbound.thread;


import kr.nanoit.domain.broker.InternalDataMapper;
import kr.nanoit.domain.broker.MetaData;
import kr.nanoit.module.borker.Broker;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * 검색 해볼것.
 * - busy waiting
 * - blocking
 */
@Slf4j
public class ReadStreamThread implements Runnable {

    private final Broker broker;
    private final BufferedReader bufferedReader;
    private final String uuid;

    public ReadStreamThread(Broker broker, BufferedReader bufferedReader, String uuid) {
        this.broker = broker;
        this.bufferedReader = bufferedReader;
        this.uuid = uuid;
    }

    @Override
    public void run() {
        log.info("[SERVER:SOCKET:{}] read start", uuid);
        boolean flag = true;
        while (flag) {
            try {
                String readData = bufferedReader.readLine();
                log.info("[SERVER:SOCKET:{}] length={} payload=[{}]", uuid, readData.length(), readData);
                if (readData != null) {
                    broker.publish(new InternalDataMapper(new MetaData(uuid), readData));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
