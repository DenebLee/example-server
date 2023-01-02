package kr.nanoit.module.inbound.socket;

import kr.nanoit.db.auth.AuthenticaionStatus;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class UserManager implements Runnable {

    private final Map<String, AuthenticaionStatus> userResourceMap;
    private final SocketManager socketManager;

    public UserManager(SocketManager socketManager) {
        this.userResourceMap = new ConcurrentHashMap<>();
        this.socketManager = socketManager;
    }

    @Override
    public void run() {
        try {
            while (true) {
                for (Map.Entry<String, AuthenticaionStatus> entry : userResourceMap.entrySet()) {
                    SocketResource socketResource = socketManager.getSocketResource(entry.getKey());

                    if (entry.getValue().equals(AuthenticaionStatus.FAILED)) {
                        socketResource.setIsAuthComplete(new AtomicBoolean(false));
                        if (unregisUser(entry.getKey())) {
                            log.warn("Usermanager unregister call");
                        }
                    }
                    if (entry.getValue().equals(AuthenticaionStatus.COMPLETE)) {
                        socketResource.setIsAuthComplete(new AtomicBoolean(true));
                        if (unregisUser(entry.getKey())) {
                            log.warn("Usermanager unregister call");
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean registUser(String uuid, AuthenticaionStatus status) {
        if (uuid == null) {
            return false;
        }
        return userResourceMap.put(uuid, status) == null;
    }

    public boolean unregisUser(String uuid) {
        if (uuid == null) {
            return false;
        }
        return userResourceMap.remove(uuid) == null;
    }

    public void replaceStatus(String uuid, AuthenticaionStatus status) {
        userResourceMap.replace(uuid, status);
    }

}
