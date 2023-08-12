package org.example;
import com.google.gson.*;
import org.json.JSONObject;

import spark.*;

public class BroadcastAPI {
    private Node node;
    private int port;

    public BroadcastAPI(Node node, int port) {
        this.node = node;
        this.port = port;
    }

    private void initializeWebServer() {
        Spark.port(port);

        Spark.post("/send-tx", (request, response) -> {
            String requestBody = request.body();

            // Parse the JSON object from the request body
            JSONObject jsonObject = new JSONObject(requestBody);
            // Extract the values for location and signalLevel
            String location = jsonObject.getString("location");
            double signalLevel = jsonObject.getDouble("signalLevel");

            // Create a new Transaction object
            Transaction transaction = new Transaction(location, signalLevel);
            node.broadcastTx(transaction);
            return "Transaction broadcasted!";
        });

        Spark.post("/send-block", (request, response) -> {
            String requestBody = request.body();

            Gson gson = new Gson();
            Block block = gson.fromJson(requestBody, Block.class);
            node.broadcastBlock(block);

            return "Block broadcasted";
        });
    }

    public void start() {
        initializeWebServer();
        Spark.awaitInitialization();
        System.out.println("API started on port " + port);
    }
}
