package kr.nanoit.abst;

import kr.nanoit.module.broker.Broker;


public abstract class ModuleProcess implements Runnable {

    public static ModuleProcessManagerImpl moduleProcessManagerImpl;

    static {
        moduleProcessManagerImpl = new ModuleProcessManagerImpl();
    }

    protected final String uuid;
    protected final Broker broker;
    protected boolean flag;

    abstract public void shoutDown();

    abstract public void sleep() throws InterruptedException;

    abstract public String getUuid();

    protected Status status;
    protected final long lastRunningTime;

    public ModuleProcess(Broker broker, String uuid) {
        this.broker = broker;
        this.uuid = uuid;
        moduleProcessManagerImpl.register(this);
        this.status = Status.INIT;
        this.lastRunningTime = System.currentTimeMillis();
    }


    public enum Status {
        INIT, //
        RUN,  // flag, interrupt 멈추는 경우
        STOP  // STOP 으로 상태가 안바뀌는 경우
    }
}
