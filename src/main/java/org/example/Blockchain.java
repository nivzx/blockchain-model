package org.example;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.example.Utils.calculateHash;

public class Blockchain {
    private List<Block> chain;
    public Map<String, Double> recordedLevels;

    // Constructor
    public Blockchain() {
        this.chain = new ArrayList<>();
        // Create the genesis block
        Block genesisBlock = createGenesisBlock();
        this.addBlock(genesisBlock);
        this.recordedLevels = new HashMap<>();
    }

    //get the previous hash
    public String getLastBlockHash() {
        return chain.get(chain.size() - 1).getHash();
    }
    public Block getLastBlock() { return chain.get(chain.size() - 1); }

    public void addBlock(Block block){
        this.chain.add(block);
    }

    // Create the genesis block
    private Block createGenesisBlock() {
        return new Block(
                0,
                new ArrayList<>(),
                "0",
                calculateHash(0, 0, "0", 0, new ArrayList<>()),
                0,
                0
        );
    }
}