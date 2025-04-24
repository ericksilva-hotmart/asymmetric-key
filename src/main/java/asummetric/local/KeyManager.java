package asummetric.local;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class KeyManager {

    private KeyManager() {
        //not constructor
    }
    private static final String ENCRYPTED_KEY_BASE64 = "9bxppxN5gJIVK3uuxwe5LcQ1yRLnaoB75Gtam9KyTJGZX1EIB6VDAhSTsk1lti8jUM0Rvk5wTefyNNZUu2bPeuRp+RJGO9odPkCswgIBfvQ="; // Chave arn:..

    static String getMasterKey() {
        return System.getenv("LOCK_DATA_KEY");
    }

    public static String getDecryptedKey(String masterKey)
            throws NoSuchPaddingException,
            NoSuchAlgorithmException, IllegalBlockSizeException,
            BadPaddingException,
            InvalidKeyException
    {
        if (masterKey == null) {
            throw new IllegalStateException("MASTER_KEY not found.");
        }

        byte[] encryptedKeyBytes = Base64.getDecoder().decode(ENCRYPTED_KEY_BASE64);
        byte[] masterKeyBytes = masterKey.getBytes(StandardCharsets.UTF_8);

        SecretKeySpec secretKey = new SecretKeySpec(masterKeyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(encryptedKeyBytes);

        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    public static String getDecryptedKey()
            throws NoSuchPaddingException,
            NoSuchAlgorithmException, IllegalBlockSizeException,
            BadPaddingException,
            InvalidKeyException
    {
        return getDecryptedKey(getMasterKey());
    }
}
