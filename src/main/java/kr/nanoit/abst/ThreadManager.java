package kr.nanoit.abst;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Slf4j
public class ThreadManager {

    public final Map<String, Process> originalObjects;
    public final Map<String, Thread> currentThreads;
    public final Map<String, Thread> testMap;

    private final ScheduledExecutorService scheduledExecutorService;

    public ThreadManager(Process mapper, Process filter, Process branch, Process sender, Process outBound, Process tcpServer) {
        this.originalObjects = new HashMap<>();
        this.currentThreads = new HashMap<>();
        this.testMap = new HashMap<>();

        register(mapper);
        register(filter);
        register(branch);
        register(sender);
        register(outBound);
        register(tcpServer);

        this.scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(this::monitor, 1000, 1000, TimeUnit.MILLISECONDS);
    }

    public void monitor() {

        for (Map.Entry<String, Process> entry : originalObjects.entrySet()) {
            if (originalObjects.containsKey(entry.getKey()) && !currentThreads.containsKey(entry.getKey())) {
                Thread thread = new Thread(originalObjects.get(entry.getKey()));
                thread.setName(entry.getKey());
                thread.start();
                currentThreads.put(entry.getKey(), thread);
            }
        }

        for (Map.Entry<String, Thread> threadEntry : currentThreads.entrySet()) {
            if (threadEntry.getValue().getState().equals(Thread.State.TERMINATED)) {
                String terminatedThreadUuid = threadEntry.getKey();

                System.out.println(threadEntry.getValue().getState() + "  " + threadEntry.getValue().getName());
                currentThreads.remove(threadEntry.getKey(), threadEntry.getValue());

                Thread restorationThread = new Thread(originalObjects.get(terminatedThreadUuid));
                restorationThread.setName(terminatedThreadUuid);
                restorationThread.start();
                currentThreads.put(terminatedThreadUuid,restorationThread);
            }
        }
    }

    public void register(Process process) {
        originalObjects.put(process.getUuid(), process);
    }

    public int getOriginalObjectSize() {
        return originalObjects.size();
    }

    public int getCurrentThreadsSize() {
        return currentThreads.size();
    }

    public void shoutDownThreadManager() {
        scheduledExecutorService.shutdown();
    }

}
