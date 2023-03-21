package org.tyler.husher.storage;

import java.util.ArrayList;
import java.util.List;

public class Blockchain {

    public Blockchain() {
        List<Block> blockchain = new ArrayList<>();
        int prefix = 4;
        String prefixString = new String(new char[prefix]).replace('\0', '0');



        Block newBlock = new Block("This is a new block.", "0", 1679242272361L);
        System.out.println("newBlock.getTimeStamp() = " + newBlock.getTimeStamp());
        System.out.println("newBlock.getHash() = " + newBlock.getHash());

        long st = System.currentTimeMillis();
        newBlock.mineBlock(prefix);
        System.out.println((System.currentTimeMillis() - st) + "ms");

        boolean shouldBeTrue = newBlock.getHash().substring(0, prefix).equals(prefixString);
        blockchain.add(newBlock);
        System.out.println("shouldBeTrue = " + shouldBeTrue);

        boolean isBlockchainValid = true;

        for (int i = 0; i < blockchain.size(); i++) {
            String previousHash = i == 0 ? "0" : blockchain.get(i - 1).getHash();
            isBlockchainValid = blockchain.get(i).getHash().equals(blockchain.get(i).calculateBlockHash())
                    && previousHash.equals(blockchain.get(i).getPreviousHash())
                    && blockchain.get(i).getHash().substring(0, prefix).equals(prefixString);

            if (!isBlockchainValid)
                break;
        }

        System.out.println(isBlockchainValid);

    }

    public static void main(String[] args) {
        new Blockchain();
    }
}
