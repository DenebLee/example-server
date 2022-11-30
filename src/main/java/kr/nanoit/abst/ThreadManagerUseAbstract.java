package kr.nanoit.abst;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ThreadManagerUseAbstract {

    public final Map<String, ModuleProcess> objectMap;
    public final Map<String, Thread> threadMap;
    private final ScheduledExecutorService scheduledExecutorService;


    public ThreadManagerUseAbstract() {
        this.objectMap = new HashMap<>();
        this.threadMap = new HashMap<>();


        this.scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(this::monitor, 1000, 1000, TimeUnit.MILLISECONDS);
    }


    public void monitor() {

        for (Map.Entry<String, ModuleProcess> entry : objectMap.entrySet()) {
            if (objectMap.containsKey(entry.getKey()) && !threadMap.containsKey(entry.getKey())) {
                Thread thread = new Thread(objectMap.get(entry.getKey()));
                thread.setName(entry.getKey());
                thread.start();
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
                threadMap.put(terminatedThreadUuid,restorationThread);
            }
        }
    }


    public int getNanoItThreadMapSize() {
        log.info("[@THREAD:MANAGER:SCHEDULER@] OBJECT-MAP SIZE => {}", objectMap.size());
        return objectMap.size();
    }

    public int getThreadMapSize() {
        log.info("[@THREAD:MANAGER:SCHEDULER@] THREAD-MAP SIZE => {}", threadMap.size());
        return threadMap.size();
    }


    public boolean isRegistered(ModuleProcess thread) {
        return objectMap.containsValue(thread);
    }


    public void interrupt(ModuleProcess thread) {
        thread.shoutDown();
    }

    public void register(String key, ModuleProcess moduleProcess) {
        objectMap.put(key, moduleProcess);
    }

    public void getStatus() {
        for (Map.Entry<String, Thread> threadEntry : threadMap.entrySet()) {
            System.out.println(threadEntry.getValue().getState());
        }
    }

}
