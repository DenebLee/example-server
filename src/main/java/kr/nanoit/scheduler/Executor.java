package kr.nanoit.scheduler;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Date;

import static org.quartz.DateBuilder.evenMinuteDate;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;
// TODO 6~7초 사이에 스케쥴러가 Company Table 에 있는 DB값을 조회 한 후 추가된 값을 검증 한후 Report로 떨어뜨려줘야함

public class Executor {
    public void startExecutor() {
        try {
            SchedulerFactory factory = new StdSchedulerFactory();
            Scheduler scheduler = factory.getScheduler();

            JobDetail jobDetail = newJob(Task.class)
                    .withIdentity("job1", Scheduler.DEFAULT_GROUP)
                    .build();

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("timetrigger", Scheduler.DEFAULT_GROUP)
                    .withSchedule(CronScheduleBuilder.cronSchedule("0/5 * * * * ?"))
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);
            scheduler.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
