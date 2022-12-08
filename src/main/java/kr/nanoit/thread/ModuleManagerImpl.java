package kr.nanoit.thread;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

// Module 을 실행하고 관리하는 객체
@Slf4j
public class ModuleManagerImpl implements ModuleManager {

    private final Map<String, Module> modulesMap;
    private final Map<String, Thread> threads;
    private final ScheduledExecutorService executorService;

    public ModuleManagerImpl() {
        this.modulesMap = new HashMap<>();
        this.threads = new HashMap<>();
        this.executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(this::monitor, 1000, 1000, TimeUnit.MILLISECONDS);
    }


    private void monitor() {
        for (Map.Entry<String, Module> entry : modulesMap.entrySet()) {
            if (modulesMap.containsKey(entry.getKey()) && !threads.containsKey(entry.getKey())) {
                Thread thread = new Thread(modulesMap.get(entry.getKey()));
                thread.setName(entry.getKey());
                thread.start();
                threads.put(entry.getKey(), thread);
            }
        }

        for (Map.Entry<String, Thread> threadEntry : threads.entrySet()) {
            if (threadEntry.getValue().getState().equals(Thread.State.TERMINATED)) {
                String terminatedThreadUuid = threadEntry.getKey();

                threads.remove(threadEntry.getKey(), threadEntry.getValue());

                Thread restorationThread = new Thread(modulesMap.get(terminatedThreadUuid));
                restorationThread.setName(terminatedThreadUuid);
                restorationThread.start();
                threads.put(terminatedThreadUuid, restorationThread);
            }

            if (modulesMap.get(threadEntry.getKey()).getRunningTime() != 0) {
                if (modulesMap.get(threadEntry.getKey()).getRunningTime() > 4000 && threadEntry.getValue().getState() == Thread.State.BLOCKED) {
                    threadEntry.getValue().interrupt();

                    threads.remove(threadEntry.getKey(), threadEntry.getValue());

                    Thread restorationThread = new Thread(modulesMap.get(threadEntry.getKey()));
                    restorationThread.setName(threadEntry.getKey());
                    restorationThread.start();
                    threads.put(threadEntry.getKey(), threadEntry.getValue());
                }
            }
        }
    }


    @Override
    public boolean register(Module... modules) {
        if (modules == null) {
            return false;
        }

        for (Module module : modules) {
            if (module.getUuid() == null) {
                return false;
            }
            if (modulesMap.containsKey(module.getUuid())) {
                return false;
            }
        }

        for (Module module : modules) {
            modulesMap.put(module.getUuid(), module);
        }

        for (Module module : modules) {
            Thread thread = new Thread(module);
            threads.put(module.getUuid(), thread);
            thread.start();
        }

        return true;
    }

    @Override
    public boolean unregister(String uuid) {
        if (uuid == null) {
            return false;
        }
        if (!modulesMap.containsKey(uuid)) {
            return false;
        }
        modulesMap.remove(uuid);
        return true;
    }

    @Override
    public long moduleTotal() {
        return modulesMap.size();
    }


    @Override
    public long total() {
        return threads.size();
    }


    @Override
    public long running() {
        return threads.entrySet().stream()
                .filter(entry -> entry.getValue().getState() != Thread.State.TERMINATED)
                .count();
    }


    @Override
    public long terminated() {
        return threads.entrySet().stream()
                .filter(entry -> entry.getValue().getState() == Thread.State.TERMINATED)
                .count();
    }


}
