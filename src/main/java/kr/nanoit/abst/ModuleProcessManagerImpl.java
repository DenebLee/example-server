package kr.nanoit.abst;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ModuleProcessManagerImpl {

    private final Map<String, ModuleProcess> objectMap;
    private final Map<String, Thread> threadMap;
    private final ScheduledExecutorService scheduledExecutorService;
    private final long DEAD_LINE = 3 * 60 * 1000L; // 1000 * 60 * 3 = 3분


    public ModuleProcessManagerImpl() {
        this.objectMap = new HashMap<>();
        this.threadMap = new ConcurrentHashMap<>();

        this.scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(this::monitor, 1000, 1000, TimeUnit.MILLISECONDS);
        monitor();
    }


    private void monitor() {
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
            if (!objectMap.containsKey(threadEntry.getKey()) && threadMap.containsKey(threadEntry)) {
                if (interruptThread(threadEntry.getKey())) {
                    threadMap.remove(threadEntry.getKey());
                }
            }
            if (threadEntry.getValue().getState().equals(Thread.State.TERMINATED) && objectMap.containsKey(threadEntry.getKey())) {
                String terminatedThreadUuid = threadEntry.getKey();
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

    public boolean register(ModuleProcess... modules) {
        if (modules == null) {
            return false;
        }

        for (ModuleProcess moduleProcess : modules) {
            if (moduleProcess.getUuid() == null) {
                return false;
            }
            if (objectMap.containsKey(moduleProcess.getUuid())) {
                return false;
            }
        }

        for (ModuleProcess moduleProcess : modules) {
            objectMap.put(moduleProcess.getUuid(), moduleProcess);
        }

        for (ModuleProcess moduleProcess : modules) {
            Thread thread = new Thread(moduleProcess);
            threadMap.put(moduleProcess.getUuid(), thread);
            thread.start();
        }
        return true;
    }

    public boolean unregister(String uuid) {
        if (uuid == null) {
            return false;
        }
        if (!objectMap.containsKey(uuid)) {
            return false;
        }
        objectMap.remove(uuid);
        if (!interruptThread(uuid)) {
            return false;
        }
        interruptThread(uuid);
        threadMap.remove(uuid);
        return true;
    }

    public long runningThreadCount() {
        return threadMap.entrySet().stream()
                .filter(stringThreadEntry -> stringThreadEntry.getValue().getState() != Thread.State.TERMINATED).count();
    }

    public boolean interruptThread(String uuid) {
        if (uuid == null) {
            return false;
        }
        threadMap.get(uuid).interrupt();
        return true;
    }

    public void shutDown() {
        scheduledExecutorService.shutdown();
    }


    private long getCurrentTime(String uuid) {
        return objectMap.get(uuid).lastRunningTime;
    }

    public boolean isOverDeadLine(String key, long deadLine) {
        return (System.currentTimeMillis() - getCurrentTime(key)) > deadLine;
    }

    public long calculateDeadLine(String key) {
        System.out.println("DeadLine 계산 -> " + DEAD_LINE + (objectMap.get(key).lastRunningTime * 1000L));
        return DEAD_LINE + (getCurrentTime(key) * 1000L);
    }

    private boolean isSetCurrentTime(String key) {
        return objectMap.get(key).lastRunningTime > 0;
    }
}
