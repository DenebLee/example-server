package kr.nanoit.domain.thread;

import lombok.Data;

@Data
public class ThreadStatus {
    private String uuid;
    private boolean readThreadStatus;
    private boolean writeThreadStatus;
}
