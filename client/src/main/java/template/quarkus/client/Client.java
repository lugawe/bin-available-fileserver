package template.quarkus.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;

public class Client {

    private static String SERVER_URL = "http://localhost:8080/api/file";
    private static final int CONTROL_PORT = 9000;

    public static void main(String[] args) throws Exception {
        Random random = new Random();
        HttpClient client = HttpClient.newHttpClient();

        // 1 Start Control Socket thread
        new Thread(Client::controlSocketListener).start();

        // 2 Register client with main server (run once)
        String clientInfo = "{\"ip\":\"127.0.0.1\", \"port\":" + CONTROL_PORT + "}";
        HttpRequest registerRequest = HttpRequest.newBuilder()
                .uri(URI.create(SERVER_URL + "/register")) // main server endpoint
                .POST(HttpRequest.BodyPublishers.ofString(clientInfo))
                .build();
        client.send(registerRequest, HttpResponse.BodyHandlers.discarding());
        System.out.println("Registered client with main server at " + SERVER_URL);


        // 3 Main read/write loop
        while (true) {
            if (random.nextBoolean()) {
                // READ
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(SERVER_URL + "/read"))
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("READ: " + response.body());
            } else {
                // WRITE
                String content = "value-" + System.currentTimeMillis();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(SERVER_URL + "/write"))
                        .POST(HttpRequest.BodyPublishers.ofString(content))
                        .build();

                client.send(request, HttpResponse.BodyHandlers.discarding());
                System.out.println("WRITE: " + content);
            }

            Thread.sleep(1000);
        }
    }

    private static void controlSocketListener() {
        try (ServerSocket serverSocket = new ServerSocket(CONTROL_PORT)) {
            System.out.println("Control Socket listening on port " + CONTROL_PORT);

            while (true) {
                try (Socket socket = serverSocket.accept();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                    String line = reader.readLine();
                    if (line != null && line.contains("new_main")) {
                        // simple parsing with JSON library
                        String newMain = line.split(":")[1].replaceAll("[\"}]", "").trim();
                        SERVER_URL = newMain + "/api/file";
                        System.out.println("Updated SERVER_URL to " + SERVER_URL);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
