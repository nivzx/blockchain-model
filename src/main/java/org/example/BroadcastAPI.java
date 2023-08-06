package org.example;

import spark.*;

public class BroadcastAPI {
    private Node node;
    private int port;

    public BroadcastAPI(Node node, int port) {
        this.node = node;
        this.port = port;
        initializeWebServer();
    }

    private void initializeWebServer() {
        Spark.port(port);

        Spark.post("/send-message", (request, response) -> {
            String message = request.body();
            node.broadcastMessage(message);
            return "Message broadcasted!";
        });
    }

    public void start() {
        Spark.awaitInitialization();
        System.out.println("API started on port " + port);
    }
}
