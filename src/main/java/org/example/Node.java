package org.example;

import com.google.gson.Gson;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.util.*;

import static org.example.Utils.calculateHash;

public class Node {
    private List<String> peersIPAddresses;
    private List<Transaction> txPool;
    private ServerSocket serverSocket;
    private final String ipAddress;
    private Blockchain blockchain;
    private int miningBlockIndex = -1; // Initialize to -1 when no mining is happening
    private Thread miningThread;


    public Node(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.txPool = new ArrayList<>();
        peersIPAddresses = new ArrayList<>();
        this.blockchain = new Blockchain();
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
    public void mineBlock() {
        miningBlockIndex = blockchain.getLastBlock().getBlockIndex() + 1;
        miningThread = new Thread(this::performMining);
        miningThread.start();
    }
    private synchronized void performMining() {

        int transactionsPerBlock = 4;
        List<Transaction> transactionsForBlock = new ArrayList<>();
        for (int i = 0; i < transactionsPerBlock && !this.txPool.isEmpty(); i++) {
            Transaction transaction = this.txPool.get(i); // Get a transaction from the pool
            this.removeFromPool(transaction);
            transactionsForBlock.add(transaction);
        }

        Block previousBlock = this.blockchain.getLastBlock();
        int index = previousBlock.getBlockIndex() + 1;
        long timestamp = System.currentTimeMillis();
        String previousHash = previousBlock.getHash();
        int nonce = 0;
        String hash = calculateHash(index, timestamp, previousHash, nonce, transactionsForBlock );

        // Adjust the difficulty level based on the number of data objects
        int difficulty = 6;

        // Proof of work - Find a valid hash by adjusting the nonce
        while (!hash.substring(0, difficulty).equals("0".repeat(difficulty))) {
            nonce++;
            hash = calculateHash(index, timestamp, previousHash, nonce, transactionsForBlock);
        }

        Block newBlock = new Block(index, transactionsForBlock, previousHash, hash, nonce, timestamp);

        for (Transaction tx : transactionsForBlock) {
            this.blockchain.recordedLevels.put(tx.getLocation(), tx.getSignalLevel());
        }

        // If the level of data is higher than 10, call the external API
        if (transactionsForBlock.size() > 10) {
        //    callExternalAPI();
        }
        miningBlockIndex = -1;
        broadcastBlock(newBlock);
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

                // Check last hash of the blockchain and add block to the blockchain
                String lastBlockHash = this.blockchain.getLastBlockHash();

                if (lastBlockHash.equals(block.getPreviousHash())){
                    this.blockchain.addBlock(block);

                    // Check if the mining thread is mining the same indexed block
                    if (miningBlockIndex != -1 && miningBlockIndex == block.getBlockIndex()) {
                        interruptMining(); // Stop the current mining thread
                        mineBlock();     // Start mining for the updated blockchain
                    }

                    // Remove block's tx from the tx pool
                    for (Transaction tx : block.getTransactions()) {
                        if (this.txPool.contains(tx)) {
                            this.removeFromPool(tx);
                        }
                    }
                }
            }

            System.out.println("Received message from peer " + clientSocket.getInetAddress() + ": " + message);
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void interruptMining() {
        if (miningThread != null && miningThread.isAlive()) {
            miningThread.interrupt(); // Interrupt the mining thread if it's running
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
