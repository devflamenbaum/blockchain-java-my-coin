import com.google.gson.GsonBuilder;

import java.util.ArrayList;

public class MyChain {

    public static ArrayList<Block> blockchain = new ArrayList<>();

    public static void main(String[] args) {

        blockchain.add(new Block("First block", "0"));
        blockchain.add(new Block("Second block", blockchain.get(blockchain.size() - 1).hash));
        blockchain.add(new Block("Third block", blockchain.get(blockchain.size() - 1).hash));

        String blockChainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);

        System.out.println(blockChainJson);
    }
}
