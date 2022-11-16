package kr.nanoit.module.inbound.socket;

import lombok.extern.slf4j.Slf4j;

// 소켓 관리  정리 밖에서 몰라야함 밖에서 요청만
// uuid에 해당하는 socket이 있는지 없는지 있으면 줌

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SocketManager implements Runnable {
    private final Map<String, SocketResource> socketResources;

    public SocketManager() {
        this.socketResources = new ConcurrentHashMap<>();
    }

    public void register(SocketResource socketResource) {
        if (socketResource != null) {
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

    public int getSocketResourcesSize() {
        return socketResources.size();
    }

    public void terminatedSocketMap(String uuid) {
        if (socketResources.containsKey(uuid)) {
            socketResources.remove(uuid);
        }
    }

    @Override
    public void run() {
        while (true) {
            for (Map.Entry<String, SocketResource> entry : socketResources.entrySet()) {
                log.info("[@SOCKET:MANAGER@] key={} isTerminated={}", entry.getKey(), entry.getValue().isTerminated());
            }
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
