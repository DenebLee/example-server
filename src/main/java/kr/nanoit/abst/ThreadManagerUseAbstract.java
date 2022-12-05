package kr.nanoit.abst;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ThreadManagerUseAbstract {

    private final Map<String, ModuleProcess> objectMap;
    private final Map<String, Thread> threadMap;
    private final ScheduledExecutorService scheduledExecutorService;
    private final long DEAD_LINE = 3 * 60 * 1000L; // 1000 * 60 * 3 = 3분


    public ThreadManagerUseAbstract() {
        this.objectMap = new HashMap<>();
        this.threadMap = new ConcurrentHashMap<>();

        this.scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(this::monitor, 1000, 1000, TimeUnit.MILLISECONDS);
    }


    public void monitor() {
        for (Map.Entry<String, ModuleProcess> entry : objectMap.entrySet()) {
            if (objectMap.containsKey(entry.getKey()) && !threadMap.containsKey(entry.getKey()) && entry.getValue().status == ModuleProcess.Status.INIT) {
                Thread thread = new Thread(objectMap.get(entry.getKey()));
                thread.setName(entry.getKey());
                thread.start();
                entry.getValue().status = ModuleProcess.Status.RUN;
                threadMap.put(entry.getKey(), thread);
            }

        }

        for (Map.Entry<String, Thread> threadEntry : threadMap.entrySet()) {
            if (threadEntry.getValue().getState().equals(Thread.State.TERMINATED)) {
                String terminatedThreadUuid = threadEntry.getKey();

                System.out.println(threadEntry.getValue().getState() + "  " + threadEntry.getValue().getName());
                threadMap.remove(threadEntry.getKey(), threadEntry.getValue());

                Thread restorationThread = new Thread(objectMap.get(terminatedThreadUuid));
                restorationThread.setName(terminatedThreadUuid);
                restorationThread.start();
                threadMap.put(terminatedThreadUuid, restorationThread);

            } else if (threadEntry.getValue().getState().equals(Thread.State.BLOCKED)) {
                // 블락된 경우
                // 교착상태에 빠지면 자원을 선점하고 있기에 스레드는 blocking 상테

                long eachThreadDeadLine = calculateDeadLine(threadEntry.getKey());
                if (isSetCurrentTime(threadEntry.getKey()) && isOverDeadLine(threadEntry.getKey(), eachThreadDeadLine)) {
                    // 실행시간 기록된 Thread 일 경우  - 각 스레드의 실행시간이 DeadLine 을 넘겼는지 계산된 값을 isOverDeadLine 에 넣었을때 true 일 경우

                    // Thread 의 종료  및 재실행?
                    threadEntry.getValue().interrupt();
                    if (!threadEntry.getValue().getState().equals(Thread.State.TERMINATED)) {
                        objectMap.get(threadEntry.getKey()).shoutDown();
                    }
                }
            }
        }
    }

    public int getObjectMapSize() {
        log.info("[@THREAD:MANAGER:SCHEDULER@] OBJECT-MAP SIZE => {}", objectMap.size());
        return objectMap.size();
    }

    public int getThreadMapSize() {
        log.info("[@THREAD:MANAGER:SCHEDULER@] THREAD-MAP SIZE => {}", threadMap.size());
        return threadMap.size();
    }

    public void register(String key, ModuleProcess moduleProcess) {
        objectMap.put(key, moduleProcess);
    }

    public void shutDown() {
        scheduledExecutorService.shutdown();
    }

    public Map<String, Thread> getThreadMap() {
        return threadMap;
    }

    public Map<String, ModuleProcess> getObjectMap() {
        return objectMap;
    }

    private long getCurrentTime(String uuid) {
        return objectMap.get(uuid).lastRunningTime;
    }

    public boolean isOverDeadLine(String key, long deadLine) {
        return (System.currentTimeMillis() - getCurrentTime(key)) > deadLine;
    }

    public long calculateDeadLine(String key) {
        System.out.println("DeadLine 계산 -> " + DEAD_LINE + (getObjectMap().get(key).lastRunningTime * 1000L));
        return DEAD_LINE + (getCurrentTime(key) * 1000L);

        // 처음 실행 시간 + DeadLine 을 더한 값
    }

    private boolean isSetCurrentTime(String key) {
        return getObjectMap().get(key).lastRunningTime > 0;
    }
}
