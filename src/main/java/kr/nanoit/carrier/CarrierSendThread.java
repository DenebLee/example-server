//package kr.nanoit.carrier;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import kr.nanoit.domain.broker.InternalDataCarrier;
//import kr.nanoit.extension.Jackson;
//import lombok.extern.slf4j.Slf4j;
//
//import java.io.BufferedWriter;
//import java.io.IOException;
//import java.util.concurrent.LinkedBlockingQueue;
//import java.util.function.Consumer;
//
//@Slf4j
//public class CarrierSendThread implements Runnable {
//
//    private final Consumer<String> cleaner;
//    private final BufferedWriter bufferedWriter;
//    private final LinkedBlockingQueue<InternalDataCarrier> queue;
//    private final ObjectMapper objectMapper;
//
//    public CarrierSendThread(Consumer<String> cleaner, LinkedBlockingQueue<InternalDataCarrier> queue, BufferedWriter bufferedWriter) {
//        this.cleaner = cleaner;
//        this.bufferedWriter = bufferedWriter;
//        this.queue = queue;
//        this.objectMapper = Jackson.getInstance().getObjectMapper();
//    }
//
//    @Override
//    public void run() {
//        log.info("[SERVER : SOCKET : {}] WRITE START", 123123);
//
//        try {
//            while (true) {
//                InternalDataCarrier internalDataCarrier = queue.poll();
//                String payloadToAgent = objectMapper.writeValueAsString(internalDataCarrier);
//
//                if (internalDataCarrier != null) {
//                    if (send(payloadToAgent)) {
//                        log.info("[SERVER : SOCKET : {}] WRITE SUCCESS! => Payload : {}", 123123, payloadToAgent);
//                    }
//                }
//
//            }
//        } catch (Throwable e) {
//
//            e.printStackTrace();
//        }
//    }
//
//    private boolean send(String data) throws IOException {
//        bufferedWriter.write(data + "\r\n");
//        bufferedWriter.flush();
//        return true;
//    }
//}
