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

    /*
    schedule(Runnable command, long delay, TimeUnit unit) : 일정 시간이 지나면 태스크를 1회 실행
    schedule(호출 가능 command, long delay, TimeUnit 단위) : 일정 시간이 지나면 작업을 1회 실행하고 결과를 반환
    scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) : 일정한 시간 간격으로 작업을 반복 실행합니다.
    scheduleWithFixedDelay(Runnable command, long initialDelay, long period, TimeUnit unit) : 태스크가 완료되면 일정 시간 후에 다시 실행
     */


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
        for (Map.Entry<String, NanoItThread> entry : nanoItThreadMap.entrySet()) {
//            log.info("[@THREAD:MANAGER:SCHEDULER@] key={} value={}", entry.getKey(), entry.getValue());
//            log.info("[@THREAD:MANAGER:SCHEDULER@] status = {}", entry.getValue().getState());
            if (entry.getValue().getState() == Thread.State.RUNNABLE) {
                System.out.println("================================================================================================================");
                System.out.println("Runnable 인 스레드 : " + entry.getValue());
                System.out.println("================================================================================================================");
            }

//            if (entry.getValue().getState() == Thread.State.WAITING) {
//
//            }
            // TODO 스레드가 정상인지 확인
            // TODO 스레드가 비정상이면 재실행 or 선택

            // TODO 스레드 정상 판별 로직

            // TODO 스레드가 비정상이면 재실행 or 선택 로직
        }
    }

    private void getNanoItThreadMapSize() {
        log.info("[@THREAD:MANAGER:SCHEDULER@] THREAD-MAP SIZE => {}", nanoItThreadMap.size());
        // TODO 사이즈 체크후 5개가 안될 경우 찾아내서 act
    }

    private void reStart() {

    }

    private boolean isRegistered(NanoItThread thread) {
        return nanoItThreadMap.containsValue(thread);
    }

//    private boolean isTerminated(NanoItThread thread) {
//
//    }


}
