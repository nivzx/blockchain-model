package org.example;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Blockchain {
    private List<Block> chain;
    private Map<String, Double> recordedLevels;

    // Constructor
    public Blockchain() {
        this.chain = new ArrayList<>();
        // Create the genesis block
        Block genesisBlock = createGenesisBlock();
        this.chain.add(genesisBlock);
        this.recordedLevels = new HashMap<>();
    }

    // Create the genesis block
    private Block createGenesisBlock() {
        Block genesisBlock = new Block(0,new ArrayList<>(),"0","",0);
        long gTime = genesisBlock.getTimestamp();

        String gHash = calculateHash(
                genesisBlock.getBlockIndex(),
                gTime,
                genesisBlock.getHash(),
                genesisBlock.getTransactions(),
                genesisBlock.getNonce());

        genesisBlock.setHash(gHash);

        return genesisBlock;
    }

    // Calculate the hash of a block
    private String calculateHash(int index, long timestamp, String previousHash, List<Transaction> tx, int nonce) {
        String blockData = index + timestamp + previousHash + tx.toString() + nonce;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(blockData.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}