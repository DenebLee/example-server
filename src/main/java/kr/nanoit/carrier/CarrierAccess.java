package kr.nanoit.carrier;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
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
                System.out.println("==========================================================================================================================================");
                log.info("  AGENT CONNECT  {}", socket.getInetAddress());
                System.out.println("==========================================================================================================================================");
                CarrierSocketResource carrierSocketResource = new CarrierSocketResource(socket);
                carrierSocketResource.serve();
            }
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        }
    }
}
