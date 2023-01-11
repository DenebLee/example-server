package kr.nanoit.module.inbound.socket;

import kr.nanoit.db.auth.AuthenticaionStatus;
import kr.nanoit.dto.UserInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class UserManager implements Runnable {

    private final Map<String, UserInfo> userResourceMap;
    private final SocketManager socketManager;

    public UserManager(SocketManager socketManager) {
        this.userResourceMap = new ConcurrentHashMap<>();
        this.socketManager = socketManager;
    }

    @Override
    public void run() {
        try {
            while (true) {
                for (Map.Entry<String, UserInfo> entry : userResourceMap.entrySet()) {
                    SocketResource socketResource = socketManager.getSocketResource(entry.getKey());
                    if (entry.getValue().getStatus().equals(AuthenticaionStatus.FAILED)) {
                        socketResource.setIsAuthComplete(new AtomicBoolean(false));
                    }
                    if (entry.getValue().getStatus().equals(AuthenticaionStatus.COMPLETE)) {
                        socketResource.setIsAuthComplete(new AtomicBoolean(true));
                    }

                    String uuid = socketManager.forwardUserMap.poll(1, TimeUnit.SECONDS);
                    if (uuid != null) {
                        unregisUser(uuid);
                        log.info("[@SOCKET-{}:USER-MANAGER@] CLIENT DISCONNECTED COMPLETE", entry.getKey());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean registUser(String uuid, UserInfo userInfo) {
        if (uuid == null) {
            return false;
        }
        return userResourceMap.put(uuid, userInfo) == null;
    }

    public void unregisUser(String uuid) {
        userResourceMap.remove(uuid);
    }

    public void replaceStatus(String uuid, UserInfo userInfo) {
        userResourceMap.replace(uuid, userInfo);
    }

    public AuthenticaionStatus getAuthenticationStatus(String uuid) {
        return userResourceMap.get(uuid).getStatus();
    }

    public UserInfo getUserInfo(String uuid) {
        return userResourceMap.get(uuid);
    }

    public boolean isExistsUserInfo(String uuid, long agentId) {
        return userResourceMap.get(uuid).getAgent_id() == agentId;
    }

}
