package org.example;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;

public class Utils {
    public static String calculateHash(int index, long timestamp, String previousHash, int nonce, List<Transaction> transactions) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String blockData = Integer.toString(index) + timestamp + previousHash + nonce + transactions.toString();
            byte[] hashBytes = digest.digest(blockData.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte hashByte : hashBytes) {
                String hex = Integer.toHexString(0xff & hashByte);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
