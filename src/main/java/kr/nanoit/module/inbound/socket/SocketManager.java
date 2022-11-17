package kr.nanoit.module.inbound.socket;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SocketManager implements Runnable {
    private final Map<String, SocketResource> socketResources;

    public SocketManager() {
        this.socketResources = new ConcurrentHashMap<>();
    }

    public void register(SocketResource socketResource) {
        if (socketResource.getSocket().isBound() && socketResource.getSocket().isConnected() && socketResource.getSocket() != null) {
            socketResources.put(socketResource.getUuid(), socketResource);
            System.out.println(socketResources.size());
        }
    }

    public SocketResource getSocketUuid(String uuid) {
        if (socketResources.containsKey(uuid)) {
            return socketResources.get(uuid);
        }
        return null;
    }

    @Override
    public void run() {
        while (true) {
            try {
                for (Map.Entry<String, SocketResource> entry : socketResources.entrySet()) {
                    if (entry.getValue().isTerminated()) {
                        log.info("[@SOCKET:MANAGER@] key={} isTerminated={}", entry.getKey(), entry.getValue().isTerminated());
                        if (entry.getValue().isSocketInputStreamClose() && entry.getValue().isSocketOutputStreamClose()) {
                            entry.getValue().socketClose();
                            if (entry.getValue().getSocket().isClosed()) {
                                socketResources.remove(entry.getKey());
                            }
                        }
                    }
                    // 참조되던 생성됐던 socketResource 삭제
                    // 삭제로 인해 socketResource 를 gc가 삭제시킴
                }
                Thread.sleep(1000L);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
