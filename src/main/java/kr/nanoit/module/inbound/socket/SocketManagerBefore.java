//package kr.nanoit.module.inbound.socket;
//
//import lombok.extern.slf4j.Slf4j;
//
//import java.io.IOException;
//import java.net.InetSocketAddress;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//@Slf4j
//public class SocketManagerBefore implements Runnable {
//    private final Map<String, SocketResource> socketResources;
//
//    private boolean flag;
//
//    public SocketManagerBefore() {
//        this.socketResources = new ConcurrentHashMap<>();
//        this.flag = true;
//    }
//
//    @Override
//    public void run() {
//        try {
//            while (flag) {
//                for (Map.Entry<String, SocketResource> entry : socketResources.entrySet()) {
//                    if (entry.getValue().getSocket().isConnected() && entry.getValue().getConnectCarrier().isClosed()) {
//                        log.error("[@SOCKET-{}:MANAGER@] LOST COMMUNICATION WITH CARRIER", entry.getKey());
//                        entry.getValue().getConnectCarrier().connect(new InetSocketAddress("localhost", 54321));
//                        log.warn("[@SOCKET-{}:MANAGER@] RETRY TO CONNECT CARRIER ", entry.getKey());
//                    }
//
//                    if (entry.getValue().isTerminated()) {
//                        log.info("[@SOCKET:MANAGER@] key = {} isTerminated = {}", entry.getKey(), entry.getValue().isTerminated());
//                        if (entry.getValue().isSocketInputStreamClose() && entry.getValue().isSocketOutputStreamClose()) {
//                            entry.getValue().connectClose();
//
//                            if (entry.getValue().getSocket().isClosed() && entry.getValue().getConnectCarrier().isClosed()) {
//                                socketResources.remove(entry.getKey());
//                                log.info("[@SOCKET-{}:MANAGER@] CLIENT DISCONNECTED COMPLETE", entry.getKey());
//                            }
//                        }
//                    }
//                }
//                Thread.sleep(1000L);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            flag = false;
//            log.error("[SOCKET-MANAGER] ERROR => ", e);
//            throw new RuntimeException(e);
//        }
//    }
//
//
//    public int hashMapSize() {
//        return socketResources.size();
//    }
//
//    public void socketManagerStop() {
//        flag = false;
//    }
//
//    public boolean register(SocketResource socketResource) {
//        if (!socketResource.getSocket().isBound()) {
//            return false;
//        }
//        if (!socketResource.getSocket().isClosed()) {
//            return false;
//        }
//        if (socketResource.getSocket() == null) {
//            return false;
//        }
//        if (socketResource.getSocket().isBound() && socketResource.getSocket().isConnected() && socketResource.getSocket() != null
//                && socketResource.getConnectCarrier().isBound() && socketResource.getConnectCarrier().isConnected() && socketResource.getConnectCarrier() != null
//        ) {
//            socketResources.put(socketResource.getUuid(), socketResource);
//        }
//        return true;
//    }
//
//    public SocketResource getSocketUuid(String uuid) {
//        if (socketResources.containsKey(uuid)) {
//            return socketResources.get(uuid);
//        }
//        return null;
//    }
//
//
//}
