package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.security.MessageDigest;
import java.util.List;

public class Block {
    private int blockIndex;
    private List<Transaction> transactions;
    private String previousHash;
    private String hash;
    private int nonce;
    private long timestamp;

    // Constructor
    public Block(int blockIndex, List<Transaction> transactions, String previousHash, String hash, int nonce, long timestamp) {
        this.blockIndex = blockIndex;
        this.transactions = transactions;
        this.previousHash = previousHash;
        this.hash = hash;
        this.nonce = nonce;
        this.timestamp = timestamp;
    }

    // Getters
    public int getBlockIndex() {
        return blockIndex;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public String getPreviousHash() {
        return previousHash;
    }
    public String getHash() {
        return hash;
    }

    public int getNonce() {
        return nonce;
    }

    public long getTimestamp() {
        return timestamp;
    }

    // Setters
    public void setBlockIndex(int blockIndex) {
        this.blockIndex = blockIndex;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }
    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(this);
    }
}
