package kr.nanoit.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;


@Slf4j
public class Task implements Job {


    //TODO REPORT 떨어뜨리기 위해 접속한 클라이언트가 보냈던 메시지 갯수와 전송 성공 여부 그대로 Report 화해서 전송
    @Override
    public void execute(JobExecutionContext context) {

    }
}
