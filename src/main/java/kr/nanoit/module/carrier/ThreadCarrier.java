package kr.nanoit.module.carrier;


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/*
    무조건 받으면 성공
    5초마다 Report
*/
public class ThreadCarrier {
    private final ScheduledExecutorService scheduledExecutorService;

    public ThreadCarrier() {
        this.scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(this::insertData, 1000, 1000, TimeUnit.MILLISECONDS);
    }

    private void insertData() {

    }

}
