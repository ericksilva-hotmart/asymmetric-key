package asummetric.v2;

public class MainTest {


    public static void main(String[] args) {
        String kmsKeyArn = "arn:aws:kms:us-east-1:000000000000:key/85baa6f6-d420-4a28-855d-3f06ecebfd6f";
        LockData lockData = new LockData(kmsKeyArn);

        String original = "Sensitive data to protect";
        String ciphertext = lockData.encrypt(original);
        String decrypted = lockData.decrypt(ciphertext);

        System.out.println("Original: " + original);
        System.out.println("ciphertext: " + ciphertext);
        System.out.println("Decrypted: " + decrypted);
    }
}
