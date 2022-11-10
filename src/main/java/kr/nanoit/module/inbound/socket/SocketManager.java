package kr.nanoit.module.inbound.socket;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SocketManager {
    private final Map<String, SocketResource> socketResources;

    public SocketManager() {
        this.socketResources = new ConcurrentHashMap<>();
    }

    public void register(SocketResource socketResource) {
        if (socketResource != null) {
            socketResources.put(socketResource.getUuid(), socketResource);
        }
    }
}
