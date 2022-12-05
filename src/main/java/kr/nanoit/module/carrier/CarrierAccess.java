package kr.nanoit.module.carrier;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class CarrierAccess implements Runnable {

    private final ServerSocket serverSocket;
    private final int port;
    private boolean flag;

    public CarrierAccess() throws IOException {
        this.port = 54321;
        this.serverSocket = new ServerSocket(port);
    }

    @Override
    public void run() {
        try {
            flag = true;
            while (flag) {
                Socket socket = serverSocket.accept();
                CarrierSocketResource carrierSocketResource = new CarrierSocketResource(socket);
                carrierSocketResource.serve();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
