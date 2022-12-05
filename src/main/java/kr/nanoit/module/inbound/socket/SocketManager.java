package kr.nanoit.module.inbound.socket;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SocketManager implements Runnable {
    private final Map<String, SocketResource> socketResources;

    @Getter
    @Setter
    public boolean testResult;
    private boolean flag;

    public SocketManager() {
        this.socketResources = new ConcurrentHashMap<>();
    }

    public void register(SocketResource socketResource) {
        if (socketResource.getSocket().isBound() && socketResource.getSocket().isConnected() && socketResource.getSocket() != null) {
            socketResources.put(socketResource.getUuid(), socketResource);
        }
    }

    public SocketResource getSocketUuid(String uuid) {
        if (socketResources.containsKey(uuid)) {
            return socketResources.get(uuid);
        }
        return null;
    }


    public int hashMapSize() {
        return socketResources.size();
    }

    public void socketManagerStop() {
        flag = false;
    }


    @Override
    public void run() {
        try {
            flag = true;
            while (flag) {
                for (Map.Entry<String, SocketResource> entry : socketResources.entrySet()) {
                    if (entry.getValue().isTerminated()) {
                        log.info("[@SOCKET:MANAGER@] key = {} isTerminated = {}", entry.getKey(), entry.getValue().isTerminated());
                        if (entry.getValue().isSocketInputStreamClose() && entry.getValue().isSocketOutputStreamClose()) {
                            setTestResult(true);
                            entry.getValue().socketClose();
                            if (entry.getValue().getSocket().isClosed()) {
                                socketResources.remove(entry.getKey());
                                log.info("[@SOCKET:MANAGER@] CLIENT DISCONNECTED COMPLETE");
                            }
                        }
                    }
                }
                Thread.sleep(1000L);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            flag = false;
            setTestResult(false);
            log.error("[SOCKET-MANAGER] ERROR => ", e);
            throw new RuntimeException(e);
        }
    }
}
