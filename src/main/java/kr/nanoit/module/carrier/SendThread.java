package kr.nanoit.module.carrier;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.nanoit.domain.broker.InternalDataCarrier;
import kr.nanoit.extension.Jackson;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class SendThread implements Runnable {

    private final Consumer<String> cleaner;
    private final DataOutputStream dataOutputStream;
    private final LinkedBlockingQueue<InternalDataCarrier> queue;
    private final ObjectMapper objectMapper;

    public SendThread(Consumer<String> cleaner, LinkedBlockingQueue<InternalDataCarrier> queue, DataOutputStream dataOutputStream) {
        this.cleaner = cleaner;
        this.dataOutputStream = dataOutputStream;
        this.queue = queue;
        this.objectMapper = Jackson.getInstance().getObjectMapper();
    }

    @Override
    public void run() {
        try {
            while (true) {
                InternalDataCarrier internalDataCarrier = queue.poll(500, TimeUnit.MICROSECONDS);
                String payloadToAgent = objectMapper.writeValueAsString(internalDataCarrier);
                if(internalDataCarrier != null){
                    dataOutputStream.write(payloadToAgent.getBytes(StandardCharsets.UTF_8));
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
