package kr.nanoit.abst;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadProcess2 extends NanoItThread {

    public ThreadProcess2(String uuid) {
        super(uuid);
    }

    @Override
    public void execute() {
        while (true) {
            log.info(uuid);
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
