import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


public class TestKey {


    public static void main(String[] args) throws Exception {
        String masterKey ="DRx9SZhe9lZvXYJH"; // 16 bytes para AES-128
        System.out.println("Key "+ masterKey);
        String keyToEncrypt = "arn:aws:kms:us-east-1:....:key/....";

        byte[] keyBytes = keyToEncrypt.getBytes(StandardCharsets.UTF_8);
        byte[] masterKeyBytes = masterKey.getBytes(StandardCharsets.UTF_8);

        SecretKeySpec secretKey = new SecretKeySpec(masterKeyBytes, "AES");

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        byte[] encryptedBytes = cipher.doFinal(keyBytes);

        String encryptedBase64 = Base64.getEncoder().encodeToString(encryptedBytes);

        System.out.println("Encrypted key (Base64): " + encryptedBase64);
    }
}
