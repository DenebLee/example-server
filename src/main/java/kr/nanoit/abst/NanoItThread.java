package kr.nanoit.abst;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.nanoit.module.broker.Broker;

public abstract class NanoItThread {

    public static ThreadManager threadManager;

    static {
        threadManager = new ThreadManager();
    }

    protected final String uuid;
    protected final Broker broker;

    protected final Thread thread;
    protected final ObjectMapper objectMapper;
    protected boolean flag;


    abstract public void execute();

    abstract public void shoutDown();

    abstract public Thread.State getState();

    abstract public void sleep() throws InterruptedException;


    public NanoItThread(Broker broker, String uuid) {
        this.objectMapper = new ObjectMapper();
        this.broker = broker;
        this.uuid = uuid;
        this.thread = new Thread(this::execute);
        this.thread.start();
        threadManager.register(uuid, this);
    }
}
