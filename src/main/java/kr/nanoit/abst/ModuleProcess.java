package kr.nanoit.abst;

import kr.nanoit.module.broker.Broker;

import java.time.LocalDateTime;

public abstract class ModuleProcess implements Runnable {

    public static ThreadManagerUseAbstract threadManagerUseAbstract;

    static {
        threadManagerUseAbstract = new ThreadManagerUseAbstract();
    }

    protected final String uuid;
    protected final Broker broker;
    protected boolean flag;


    abstract public void shoutDown();


    abstract public void sleep() throws InterruptedException;

    protected final Status status;
    protected final LocalDateTime lastRunningTime;

    public ModuleProcess(Broker broker, String uuid) {
        this.broker = broker;
        this.uuid = uuid;
        threadManagerUseAbstract.register(uuid, this);
        this.status = Status.INIT;
        this.lastRunningTime = LocalDateTime.now();
    }


    public enum Status {
        INIT, //
        RUN,  // flag, interrupt 멈추는 경우
        STOP  // STOP으로 상태가 안바뀌는 경우
    }
}
