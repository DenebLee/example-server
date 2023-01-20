package kr.nanoit.Client;

public class ClientMonitor implements Runnable {


    @Override
    public void run() {
        while (true) {
            System.out.println("WRITE: " + TcpClientApplication.writeCounter.get() + " READ: " + TcpClientApplication.readCounter.get() + " SA_SUCC: " + TcpClientApplication.sendAckSuccessCounter.get() + " SA_FAIL: " + TcpClientApplication.sendAckFailCounter.get() + " RE_SUCC: " + TcpClientApplication.reportSuccessCounter.get() + " RE_FAIL: " + TcpClientApplication.reportFailCounter.get());
            try {
                Thread.sleep(500L);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
        System.out.println("Monitor Thread 정상 종료");
    }
}