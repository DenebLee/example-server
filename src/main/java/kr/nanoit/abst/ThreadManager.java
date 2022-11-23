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
        scheduledExecutorService.scheduleAtFixedRate(this::monitor, 1, 500, TimeUnit.MILLISECONDS);
    }

    public void register(String key, NanoItThread nanoItThread) {
        nanoItThreadMap.put(key, nanoItThread);
    }

    public void monitor() {
        for (Map.Entry<String, NanoItThread> entry : nanoItThreadMap.entrySet()) {
            if (isRegistered(entry.getValue())) {
                if (entry.getValue().getState() == Thread.State.RUNNABLE) {
                    System.out.println("RUNNABLE 인 스레드 발견  => " + entry.getValue() + entry.getValue().getState());
                }
                if (isTerminated(entry.getValue())) {
                    // 이미 죽은 스레드는 재 실행 불가능하고 새 객체를 생성하고 시작해야되는데 속도가 떨어진다
                }
                if (entry.getValue().getState() == Thread.State.TIMED_WAITING) {
                    log.info("[@THREAD:MANAGER:SCHEDULER@] WAIT STATE THREADS => {}  STATE : {}", entry.getValue(), entry.getValue().getState());
                }
            }

        }

    }

    public int getNanoItThreadMapSize() {
        log.info("[@THREAD:MANAGER:SCHEDULER@] THREAD-MAP SIZE => {}", nanoItThreadMap.size());
        return nanoItThreadMap.size();
    }


    public boolean isRegistered(NanoItThread thread) {
        return nanoItThreadMap.containsValue(thread);
    }

    public boolean isTerminated(NanoItThread thread) {
        return thread.getState() == Thread.State.TERMINATED;
    }


}
