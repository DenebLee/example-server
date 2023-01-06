package kr.nanoit.scheduler;

import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

public class Executor {


    public void startExecutor() {
        try {
            SchedulerFactory factory = new StdSchedulerFactory();
            Scheduler scheduler = factory.getScheduler();

            scheduler.getListenerManager().addJobListener().currentTrhead():


            scheduler.getListenerManager().
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
