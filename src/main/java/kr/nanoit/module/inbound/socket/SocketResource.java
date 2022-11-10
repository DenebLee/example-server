package kr.nanoit.module.inbound.socket;

import kr.nanoit.module.borker.Broker;
import kr.nanoit.module.inbound.thread.ReadStreamThread;
import kr.nanoit.module.inbound.thread.WriteStreamThread;
import lombok.Data;

import java.io.*;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

@Data
public class SocketResource {

    private final String uuid;
    private final Socket socket;
    private final Broker broker;
    private final Thread readStreamThread;
    private final Thread writeStreamThread;
    private final BufferedReader bufferedReader;
    private final BufferedWriter bufferedWriter;

    public SocketResource(Socket socket, Broker broker) throws IOException {
        this.uuid = UUID.randomUUID().toString();
        this.socket = socket;
        this.broker = broker;

        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        this.readStreamThread = new Thread(new ReadStreamThread(broker, bufferedReader, uuid));
        this.writeStreamThread = new Thread(new WriteStreamThread(broker, bufferedWriter, uuid));
    }

    public void serve() throws IOException {
        readStreamThread.start();
        writeStreamThread.start();
    }
}