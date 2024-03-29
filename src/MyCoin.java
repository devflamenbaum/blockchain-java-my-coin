public class MyCoin {

    public static void main(String[] args) {
        Block initialBlock = new Block("Initial block", "0");
        System.out.println("Hash for block 1: " + initialBlock.hash);

        Block secondBlock = new Block("Second block", initialBlock.hash);
        System.out.println("Hash for block 2: " + secondBlock.hash);

        Block thirdBlock = new Block("Third block", secondBlock.hash);
        System.out.println("Hash for block 3: " + thirdBlock.hash);
    }
}
