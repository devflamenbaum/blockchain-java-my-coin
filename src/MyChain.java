import com.google.gson.GsonBuilder;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;

public class MyChain {

    public static ArrayList<Block> blockchain = new ArrayList<>();
    public static HashMap<String, TransactionOutput> UTXOs = new HashMap<>();
    public static int difficulty = 5;
    public static float minimumTransaction = 0.1f;
    public static Wallet walletA;
    public static Wallet walletB;
    public static Transaction initialTransaction;

    public static void main(String[] args) {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        walletA = new Wallet();
        walletB = new Wallet();

        Wallet coinbase = new Wallet();

        initialTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, 100f, null);
        initialTransaction.generateSignature(coinbase.privateKey);
        initialTransaction.transactionId = "0";
        initialTransaction.outputs.add(new TransactionOutput(
                initialTransaction.reciepient, initialTransaction.value, initialTransaction.transactionId));
        UTXOs.put(initialTransaction.outputs.get(0).id, initialTransaction.outputs.get(0));

        System.out.println("Creating and Mining Initial block... ");

        Block initialBlock = new Block("0");

        initialBlock.addTransaction(initialTransaction);

        addBlock(initialBlock);

        Block block1 = new Block(initialBlock.hash);
        System.out.println("\nwalletA's balance is: " + walletA.getBalance());
        System.out.println("\nwalletA is Attempting to send funds (40) to walletB...");
        block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
        addBlock(block1);
        System.out.println("\nwalletA's balance is: " + walletA.getBalance());
        System.out.println("\nwalletB's balance is: " + walletB.getBalance());

        Block block2 = new Block(block1.hash);
        System.out.println("\nwalletA Attempting to send more funds (1000) than it has...");
        block2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f));
        addBlock(block2);
        System.out.println("\nwalletA's balance is: " + walletA.getBalance());
        System.out.println("\nwalletB's balance is: " + walletB.getBalance());

        Block block3 = new Block(block2.hash);
        System.out.println("\nwalletB is Attempting to send funds (20) to walletA...");
        block3.addTransaction(walletB.sendFunds(walletA.publicKey, 20f));
        addBlock(block3);
        System.out.println("\nwalletA's balance is: " + walletA.getBalance());
        System.out.println("\nwalletB's balance is: " + walletB.getBalance());

        isChainValid();


    }

    private static void addBlock(Block initialBlock) {
        initialBlock.mineBlock(difficulty);
        blockchain.add(initialBlock);
    }


    public static Boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;

        String hashTarget = new String(new char[difficulty]).replace('\0', '0');
        HashMap<String, TransactionOutput> tempUTXOs = new HashMap<>();
        tempUTXOs.put(initialTransaction.outputs.get(0).id, initialTransaction.outputs.get(0));

        for (int i = 1; i < blockchain.size(); i++) {
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i - 1);
            if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
                System.out.println("Current Hashes not equal");
                return false;
            }
            if (!previousBlock.hash.equals(currentBlock.previousHash)) {
                System.out.println("Previous Hashes not equal");
                return false;
            }

            if (!currentBlock.hash.substring(0, difficulty).equals(hashTarget)) {
                System.out.println("This block hasn't been mined");
                return false;
            }

            TransactionOutput tempOutput;
            for (int t = 0; t < currentBlock.transactions.size(); t++) {
                Transaction currentTransaction = currentBlock.transactions.get(t);

                if (!currentTransaction.verifySignature()) {
                    System.out.println("#Signature on Transaction(" + t + ") is Invalid");
                    return false;
                }
                if (currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
                    System.out.println("#Inputs are note equal to outputs on Transaction(" + t + ")");
                    return false;
                }

                for (TransactionInput input : currentTransaction.inputs) {
                    tempOutput = tempUTXOs.get(input.transactionOutputId);

                    if (tempOutput == null) {
                        System.out.println("#Referenced input on Transaction(" + t + ") is Invalid");
                        return false;
                    }

                    if(input.UTXO.value != tempOutput.value) {
                        System.out.println("#Referenced input on Transaction(" + t + ") value is Invalid");
                        return false;
                    }

                    tempUTXOs.remove(input.transactionOutputId);
                }

                for (TransactionOutput output : currentTransaction.outputs) {
                    tempUTXOs.put(output.id, output);
                }

                if( currentTransaction.outputs.get(0).reciepient != currentTransaction.reciepient) {
                    System.out.println("#Transaction(" + t + ") output reciepient is not who it should be");
                    return false;
                }

                if( currentTransaction.outputs.get(1).reciepient != currentTransaction.sender) {
                    System.out.println("#Transaction(" + t + ") output change is not a sender");
                    return false;
                }
            }
        }

        System.out.println("Blockchain is valid");
        return true;
    }
}
