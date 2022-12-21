//package kr.nanoit.carrier;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import kr.nanoit.domain.broker.InternalDataCarrier;
//import kr.nanoit.domain.payload.Payload;
//import kr.nanoit.domain.payload.PayloadType;
//import kr.nanoit.domain.payload.Send;
//import kr.nanoit.domain.payload.SendAck;
//import kr.nanoit.extension.Jackson;
//import lombok.extern.slf4j.Slf4j;
//
//import java.io.BufferedReader;
//import java.util.concurrent.LinkedBlockingQueue;
//import java.util.function.Consumer;
//
//@Slf4j
//public class CarrierReadThread implements Runnable {
//
//    private final Consumer<String> cleaner;
//    private final BufferedReader bufferedReader;
//    private final LinkedBlockingQueue<InternalDataCarrier> queue;
//    private final ObjectMapper objectMapper;
//
//    public CarrierReadThread(Consumer<String> cleaner, LinkedBlockingQueue<InternalDataCarrier> queue, BufferedReader bufferedReader) {
//        this.cleaner = cleaner;
//        this.bufferedReader = bufferedReader;
//        this.queue = queue;
//        this.objectMapper = Jackson.getInstance().getObjectMapper();
//    }
//
//    @Override
//    public void run() {
//        log.info("[SERVER : SOCKET : {}] READ START", 123123);
//
//        try {
//            while (true) {
//                InternalDataCarrier internalDataCarrier = objectMapper.readValue(bufferedReader.readLine(), InternalDataCarrier.class);
//
//                Send send = objectMapper.convertValue(internalDataCarrier.getPayload().getData(), Send.class);
//                SendAck sendAck;
//                if (send.getId() == 0 || send.getCallback() == null || send.getContent() == null || send.getPhone() == null || send.getPhone() == "") {
//                    sendAck = new SendAck(send.getId(), "FAIL");
//                } else {
//                    sendAck = new SendAck(send.getId(), "SUCCESS");
//                }
//                Payload sendAckPayload = new Payload(PayloadType.SEND_ACK, internalDataCarrier.getPayload().getMessageUuid(), sendAck);
//                if (queue.offer(new InternalDataCarrier(internalDataCarrier.getMetaData(), sendAckPayload))) {
//                    log.info("[SERVER : SOCKET : {}] READ SUCCESS! => Payload : {}", 123123, sendAckPayload);
//                }
//            }
//        } catch (Throwable e) {
//            log.info("[@SOCKET:READ:{}@] terminating...", 123123);
//            cleaner.accept(this.getClass().getName());
//        }
//    }
//}
