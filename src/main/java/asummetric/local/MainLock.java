package asummetric.local;

public class MainLock {

    public static void main(String[] args) {
        Lock lock = new Lock();
        String encrypt = lock.lockWithJson("teste");
        System.out.println(encrypt);
        String decrypt = lock.unlockData(encrypt);
        System.out.println(decrypt);
    }
}

