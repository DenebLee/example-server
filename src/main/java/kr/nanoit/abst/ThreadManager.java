package kr.nanoit.abst;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ThreadManager {

    private final Map<String, NanoItThread> nanoItThreadMap;
    private final ScheduledExecutorService scheduledExecutorService;

    public ThreadManager() {
        this.nanoItThreadMap = new ConcurrentHashMap<>();
        this.scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(this::monitor, 1, 1, TimeUnit.SECONDS);
//        scheduledExecutorService.scheduleWithFixedDelay(this::monitor, 10, TimeUnit.SECONDS);
    }

    public void register(String key, NanoItThread nanoItThread) {
        nanoItThreadMap.put(key, nanoItThread);
    }

    public void monitor() {
        log.info("[@THREAD:MANAGER:SCHEDULER@]");
        for (Map.Entry<String, NanoItThread> entry : nanoItThreadMap.entrySet()) {
            log.info("[@THREAD:MANAGER:SCHEDULER@] key={} value={}", entry.getKey(), entry.getValue());

            // TODO 스레드가 정상인지 확인
            // TODO 스레드가 비정상이면 재실행 or 선택
        }
    }
}
