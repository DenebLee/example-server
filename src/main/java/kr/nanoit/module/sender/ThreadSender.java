package kr.nanoit.module.sender;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.nanoit.abst.ModuleProcess;
import kr.nanoit.domain.broker.*;
import kr.nanoit.domain.payload.Payload;
import kr.nanoit.domain.payload.PayloadType;
import kr.nanoit.domain.payload.Send;
import kr.nanoit.domain.payload.SendAck;
import kr.nanoit.extension.Jackson;
import kr.nanoit.module.broker.Broker;
import lombok.extern.slf4j.Slf4j;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

@Slf4j
public class ThreadSender extends ModuleProcess {


    private final ObjectMapper objectMapper;
    private DataOutputStream dataOutputStream;


    public ThreadSender(Broker broker, String uuid) throws IOException {
        super(broker, uuid);
        this.objectMapper = Jackson.getInstance().getObjectMapper();
        if (connectCarrier.isConnected()) {
            dataOutputStream = new DataOutputStream(connectCarrier.getOutputStream());
        }
    }

    @Override
    public void run() {
        try {
            this.flag = true;
            while (this.flag) {
                Object object = broker.subscribe(InternalDataType.SENDER);

                if (object != null && object instanceof InternalDataSender) {
//                    log.info("[SENDER]   DATA INPUT => {}", object);

                    // 들어오는거 확인
                    InternalDataSender internalDataSender = (InternalDataSender) object;
                    Payload payload = ((InternalDataSender) object).getPayload();
                    System.out.println("통신사 연결 되었는 지 " + connectCarrier.isConnected());
                    String value = objectMapper.writeValueAsString(new InternalDataCarrier(internalDataSender.getMetaData(), payload)) + "\r\n";
                    if (connectCarrier.isConnected()) {
                        dataOutputStream.write(value.getBytes(StandardCharsets.UTF_8));
                        System.out.println(" 통신사 전송 완료  => " + value);
                    }
                }

            }
        } catch (InterruptedException e) {
            shoutDown();
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void shoutDown() {
        this.flag = false;
        log.warn("[SENDER   THIS THREAD SHUTDOWN]");
    }

    @Override
    public void sleep() throws InterruptedException {
        Thread.sleep(1000);
    }

    @Override
    public String getUuid() {
        return this.uuid;
    }
}
