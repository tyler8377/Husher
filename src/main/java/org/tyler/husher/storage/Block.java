package org.tyler.husher.storage;

import org.tyler.husher.util.HashUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class Block {

    private String hash;
    private String previousHash;
    private String data;
    private long timeStamp;
    private int nonce;
 
    public Block(String data, String previousHash, long timeStamp) {
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = timeStamp;
        this.hash = calculateBlockHash();
    }

    public String calculateBlockHash() {
        String dataToHash = previousHash + timeStamp + nonce + data;
        byte[] bytes = HashUtils.sha256(dataToHash.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();

        for (byte b : bytes)
            sb.append(String.format("%02x", b));

        return sb.toString();
    }

    public String mineBlock(int prefix) {
        String prefixString = new String(new char[prefix]).replace('\0', '0');
        System.out.println("prefixString = " + prefixString);
        while (!hash.substring(0, prefix).equals(prefixString)) {
            nonce++;
            hash = calculateBlockHash();
        }
        System.out.println("nonce = " + nonce);
        return hash;
    }

    public String getHash() {
        return hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public String getData() {
        return data;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public int getNonce() {
        return nonce;
    }
}