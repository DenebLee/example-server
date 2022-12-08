package kr.nanoit.abst;

import jdk.nashorn.internal.objects.annotations.Getter;
import jdk.nashorn.internal.objects.annotations.Setter;
import kr.nanoit.module.broker.Broker;

import java.net.Socket;


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

    abstract public String getUuid();

    protected Status status;
    protected final long lastRunningTime;

    public ModuleProcess(Broker broker, String uuid) {
        this.broker = broker;
        this.uuid = uuid;
        threadManagerUseAbstract.register(this);
        this.status = Status.INIT;
        this.lastRunningTime = System.currentTimeMillis();
    }


    public enum Status {
        INIT, //
        RUN,  // flag, interrupt 멈추는 경우
        STOP  // STOP 으로 상태가 안바뀌는 경우
    }
}
