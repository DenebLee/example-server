package kr.nanoit.thread;

public interface Module extends Runnable {
    String getUuid();
    long getRunningTime();
}
