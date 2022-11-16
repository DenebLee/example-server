package kr.nanoit.abst;

import java.util.Map;

public class ThreadManager {
    private final Map<String, NanoItThread> nanoItThreadMap;

    public ThreadManager(Map<String, NanoItThread> nanoItThreadMap) {
        this.nanoItThreadMap = nanoItThreadMap;
    }
}
