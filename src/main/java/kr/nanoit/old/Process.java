package kr.nanoit.old;

public interface Process extends Runnable {
    String getUuid();
    boolean getFlag();

    long getRunningTime();
}
