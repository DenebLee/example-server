package kr.nanoit.module.carrier;

import kr.nanoit.domain.broker.InternalDataCarrier;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
// TODO 통신사 모듈에선 ThreadPool 로 Task 를 만든 후 작업 처리
public class CarrierSocketResource {
    private final Socket socket;
    private final Thread readThread;
    private final Thread sendThread;
    private final LinkedBlockingQueue<InternalDataCarrier> queue;


    public CarrierSocketResource(Socket socket) throws IOException {
        this.socket = socket;
        this.queue = new LinkedBlockingQueue<>();

        this.readThread = new Thread(new ReadThread(this::readThreadCleaner, queue, new BufferedReader(new InputStreamReader(socket.getInputStream()))));
        this.sendThread = new Thread(new SendThread(this::sendThreadCleaner, queue, new DataOutputStream(socket.getOutputStream())));
    }

    public void serve() {
        readThread.start();
        sendThread.start();
    }

    public void readThreadCleaner(String calledClassName) {
        try {
            sendThread.interrupt();
            this.socket.shutdownInput();
        } catch (IOException e) {
            log.error("SOCKET INPUT STREAM CLOSE FAILED", e);
        }
    }

    public void sendThreadCleaner(String calledClassName) {
        try {
            readThread.interrupt();
            this.socket.shutdownOutput();
        } catch (IOException e) {
            log.error(" SOCKET OUT STREAM CLOSE FAILED", e);
        }

    }

}