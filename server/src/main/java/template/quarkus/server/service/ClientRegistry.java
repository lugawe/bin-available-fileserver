package template.quarkus.server.service;

import jakarta.inject.Singleton;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class ClientRegistry {
    private final Map<String, ClientInfo> clients = new ConcurrentHashMap<>();

    public void registerClient(String clientId, String ip, int port) {
        clients.put(clientId, new ClientInfo(ip, port));
        // replicate to other replicas
        replicateToReplicas(clientId, ip, port);
    }

    public Map<String, ClientInfo> getClients() {
        return clients;
    }

    private void replicateToReplicas(String clientId, String ip, int port) {
        // HTTP POST to all replicas with client info
    }

    public static class ClientInfo {
        public String ip;
        public int port;
        public ClientInfo(String ip, int port) { this.ip = ip; this.port = port; }
    }

}
