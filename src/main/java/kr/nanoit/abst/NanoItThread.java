package kr.nanoit.abst;

public abstract class NanoItThread {
    private Thread thread;

    public NanoItThread(Thread thread) {
        this.thread = thread;
        thread.start();
    }
}
