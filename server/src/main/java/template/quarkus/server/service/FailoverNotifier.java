package template.quarkus.server.service;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.io.PrintWriter;
import java.net.Socket;

@Singleton
public class FailoverNotifier {

    @Inject
    ClientRegistry clientRegistry;

    public void notifyClients(String newMainUrl) {
        for (ClientRegistry.ClientInfo client : clientRegistry.getClients().values()) {
            try (Socket socket = new Socket(client.ip, client.port);
                 PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
                writer.println("{\"new_main\":\"" + newMainUrl + "\"}");
            } catch (Exception e) {
                System.err.println("Failed to notify client " + client.ip);
            }
        }
    }

}
