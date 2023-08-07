package org.example;

import com.google.gson.Gson;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.util.*;

public class Node {
    private List<String> peersIPAddresses;
    private List<Transaction> txPool;
    private ServerSocket serverSocket;
    private final String ipAddress;
    private List<Block> blockchain;

    public Node(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.txPool = new ArrayList<>();
        peersIPAddresses = new ArrayList<>();
        try {
            serverSocket = new ServerSocket(port);
            new Thread(this::acceptConnections).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void joinNetwork(String peerIPAddress) {
        peersIPAddresses.add(peerIPAddress);
    }
    public void addToPool (Transaction tx) { this.txPool.add(tx); }
    public void removeFromPool (Transaction tx) {
        if( this.txPool.contains(tx)) {
            this.txPool.remove(tx);
        } else {
            System.out.println("Couldn't find transaction");
        }
    }

    public void broadcastTx(Transaction tx) {
        for (String peerIPAddress : peersIPAddresses) {
            sendTx(peerIPAddress, tx);
        }
    }
    public void broadcastBlock(Block block) {
        for (String peerIPAddress : peersIPAddresses) {
            sendBlock(peerIPAddress, block);
        }
    }

    private void acceptConnections() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleConnection(clientSocket)).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleConnection(Socket clientSocket) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String message = in.readLine();
            Gson gson = new Gson();
            JSONObject jsonObject = new JSONObject(message);


            if (jsonObject.has("location")) {
                Transaction tx = gson.fromJson(message, Transaction.class);
                this.addToPool(tx);
            } else if (jsonObject.has("nonce")) {
                Block block = gson.fromJson(message, Block.class);

                // Check last hash of the blockchain (validation)

                // If valid -> Remove Txs from tx pool
                for (Transaction tx : block.getTransactions()) {
                    if (this.getTxPool().contains(tx)) {
                        this.removeFromPool(tx);
                    }
                }
                System.out.println(block);

                // Add block to the blockchain
            }
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendTx(String peerIPAddress, Transaction tx) {
        try {
            Gson gson = new Gson();
            String jsonTx = gson.toJson(tx);
            Socket socket = new Socket(peerIPAddress, getPort());
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(jsonTx);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void sendBlock(String peerIPAddress, Block block) {
        try {
            Gson gson = new Gson();
            String jsonBlock = gson.toJson(block);
            Socket socket = new Socket(peerIPAddress, getPort());
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(jsonBlock);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }

    public List<Transaction> getTxPool() {
        return this.txPool;
    }
}
