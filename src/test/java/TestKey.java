import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class TestKey {


    private static final int GCM_IV_LENGTH = 12; // 12 bytes para IV
    private static final int GCM_TAG_LENGTH = 128; // bits

    private static final SecureRandom secureRandom = new SecureRandom();

    private static String encrypt() throws Exception {
        String masterKey = "DRx9SZhe9lZvXYJH"; // 16 bytes para AES-128
        System.out.println("Key " + masterKey);
        String keyToEncrypt = "arn:aws:kms:us-east-1:687664246847:key/20ece749-33f3-4b76-96c8-82991b1da003";

        byte[] keyBytes = keyToEncrypt.getBytes(StandardCharsets.UTF_8);
        byte[] masterKeyBytes = masterKey.getBytes(StandardCharsets.UTF_8);

        if (masterKeyBytes.length != 16 && masterKeyBytes.length != 24 && masterKeyBytes.length != 32) {
            throw new IllegalArgumentException("Invalid AES key length (must be 16, 24 or 32 bytes)");
        }

        SecretKeySpec secretKey = new SecretKeySpec(masterKeyBytes, "AES");

        // Gerar IV aleatório
        byte[] iv = new byte[GCM_IV_LENGTH];
        secureRandom.nextBytes(iv);

        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

        byte[] encryptedBytes = cipher.doFinal(keyBytes);

        // Prefixar IV + ciphertext para armazenamento/transmissão
        byte[] encryptedMessage = new byte[iv.length + encryptedBytes.length];
        System.arraycopy(iv, 0, encryptedMessage, 0, iv.length);
        System.arraycopy(encryptedBytes, 0, encryptedMessage, iv.length, encryptedBytes.length);

        return Base64.getEncoder().encodeToString(encryptedMessage);
    }

    public static void main(String[] args) throws Exception {
        String encrypted = encrypt();
        System.out.println("Encrypted (Base64): " + encrypted);
    }
}
