package kr.nanoit.module.inbound.socket;

import kr.nanoit.module.broker.Broker;
import kr.nanoit.module.inbound.thread.ReadStreamThread;
import kr.nanoit.module.inbound.thread.WriteStreamThread;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public class SocketResourceThread {

    private final String uuid;
    private final Thread readStreamThread;
    private final Thread writeStreamThread;

    public SocketResourceThread(String uuid, Broker broker, LinkedBlockingQueue<String> writeBuffer, Socket socket) throws IOException {
        this.uuid = uuid;
        this.readStreamThread = new Thread(new ReadStreamThread(this::readThreadCleaner, broker, new BufferedReader(new InputStreamReader(socket.getInputStream())), uuid));
        readStreamThread.setName(uuid + "-read");
        this.writeStreamThread = new Thread(new WriteStreamThread(this::writeThreadCleaner, new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), uuid, writeBuffer));
        writeStreamThread.setName(uuid + "-write");
    }

    public void start() {
        readStreamThread.start();
        writeStreamThread.start();
    }

    public void readThreadCleaner(String calledClassName) {
        log.info("[@SOCKET:RESOURCE:{}@] {}: called cleaner", uuid, calledClassName);
        writeStreamThread.interrupt();
        log.info("[@SOCKET:RESOURCE:{}@] [{}] thread read={} write={}", uuid, Thread.currentThread().getName(), readStreamThread.getState(), writeStreamThread.getState());

        // SOCKET MANAGER에서 정리 진행
        // SOCKET MANAGER에서 정리 전에 SOCKET, READ THREAD, WRITE THREAD 가 종료 됐는지 확인 후 리소스 정리
        // - HASHMAP DELETE 참조 없어짐
        // - SOCKET RESOURCE 객체에 접근할 수 있는 참조가 없는데 SOCKET RESOURCE (쓰레드나 소켓)이 살아있으면 Memory Leak
    }

    public void writeThreadCleaner(String calledClassName) {
        log.info("[@SOCKET:RESOURCE:{}@] {}: called cleaner", uuid, calledClassName);
        readStreamThread.interrupt();
        log.info("[@SOCKET:RESOURCE:{}@] [{}] thread read={} write={}", uuid, Thread.currentThread().getName(), readStreamThread.getState(), writeStreamThread.getState());
    }

    public boolean isTerminated() {
        return readStreamThread.getState().equals(Thread.State.TERMINATED) && writeStreamThread.getState().equals(Thread.State.TERMINATED);
    }
}
