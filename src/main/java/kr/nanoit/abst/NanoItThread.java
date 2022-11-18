package kr.nanoit.abst;

public abstract class NanoItThread {

    private static ThreadManager threadManager;

    static {
        threadManager = new ThreadManager();
    }

    protected final String uuid;

    protected final Thread thread;

    abstract public void execute();

    public NanoItThread(String uuid) {
        this.uuid = uuid;
        this.thread = new Thread(this::execute);
        this.thread.start();
        threadManager.register(uuid, this);
    }


}
