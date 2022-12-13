package kr.nanoit.module.inbound.socket;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SocketManager implements Runnable {
    private final Map<String, SocketResource> socketResources;

    private boolean flag;

    public SocketManager() {
        this.socketResources = new ConcurrentHashMap<>();
        this.flag = true;
    }

    @Override
    public void run() {
        try {
            while (flag) {
                for (Map.Entry<String, SocketResource> entry : socketResources.entrySet()) {
                    if (entry.getValue().isTerminated()) {
                        log.info("[@SOCKET:MANAGER@] key = {} isTerminated = {}", entry.getKey(), entry.getValue().isTerminated());
                        if (entry.getValue().isSocketInputStreamClose() && entry.getValue().isSocketOutputStreamClose()) {
                            entry.getValue().connectClose();
                            if (entry.getValue().getSocket().isClosed()) {
                                socketResources.remove(entry.getKey());
                                log.info("[@SOCKET-{}:MANAGER@] CLIENT DISCONNECTED COMPLETE", entry.getKey());
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
            log.error("[SOCKET-MANAGER] ERROR => ", e);
            throw new RuntimeException(e);
        }
    }


    public int getSocketResourcesMapSize() {
        return socketResources.size();
    }

    public void socketManagerStop() {
        flag = false;
    }

    public boolean register(SocketResource socketResource) {
        if (socketResource.getSocket() == null) {
            return false;
        }
        if (!socketResource.getSocket().isBound()) {
            return false;
        }
        if (socketResource.getSocket().isClosed()) {
            return false;
        }
        if (socketResource.getUuid() == null) {
            return false;
        }
        return socketResources.put(socketResource.getUuid(), socketResource) == null;
    }

    public SocketResource getSocketResource(String uuid) {
        if (uuid == null) {
            return null;
        }
        if (!socketResources.containsKey(uuid)) {
            return null;

        }
        return socketResources.get(uuid);
    }
}
