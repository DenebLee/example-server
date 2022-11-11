package kr.nanoit.module.inbound.thread;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.nanoit.domain.broker.InternalDataType;
import kr.nanoit.module.borker.Broker;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.IOException;

@Slf4j
public class WriteStreamThread implements Runnable {

    private final BufferedWriter bufferedWriter;
    private final Broker broker;
    private final String uuid;
    private final ObjectMapper objectMapper;

    public WriteStreamThread(Broker broker, BufferedWriter bufferedWriter, String uuid) {
        this.bufferedWriter = bufferedWriter;
        this.broker = broker;
        this.uuid = uuid;
        this.objectMapper = new ObjectMapper();

    }


    @Override
    public void run() {
        log.info("[SERVER:SOCKET:{}] write start", uuid);
        boolean flag = true;
        try {
            while (flag) {
                Object data = broker.outBound(InternalDataType.OUTBOUND, uuid);
                //TODO data object를 변환 후 JSON 전송

//                if (data != null) {
//                    if (send(data)) {
//                        log.info("[SERVER:SOCKET:{}] length={} payload=[{}]", uuid, data.length(), data);
//                    }
//                }

            }
        } catch (Exception e) {
            flag = false;
            throw new RuntimeException(e);
        }

    }

    private boolean send(String data) throws IOException {
        bufferedWriter.write(data + "\r\n");
        bufferedWriter.flush();
        return true;
    }
}
