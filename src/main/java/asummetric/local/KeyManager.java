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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeyManager {

    private static final Logger logger = LoggerFactory.getLogger(KeyManager.class);

    private KeyManager() {
        //not constructor
    }

    private static final String FALLBACK_ENCRYPTED_KEY_BASE64 = "9bxppxN5gJIVK3uuxwe5LcQ1yRLnaoB75Gtam9KyTJGZX1EIB6VDAhSTsk1lti8jUM0Rvk5wTefyNNZUu2bPeuRp+RJGO9odPkCswgIBfvQ="; // Chave arn:..
    private static final String NWE_ENCRYPTED_KEY_BASE64 = "9bxppxN5gJIVK3uuxwe5LcQ1yRLnaoB75Gtam9KyTJGhDZXLQy/S2d2Mp/C6bFgxKOx8bC94Ex7dE7TVxPeqYQ==";

    static String getMasterKey() {
        return System.getenv("LOCK_DATA_KEY");
    }

    public static String getDecryptedKey(String masterKey, boolean fallback)
            throws NoSuchPaddingException,
            NoSuchAlgorithmException, IllegalBlockSizeException,
            BadPaddingException,
            InvalidKeyException {
        if (masterKey == null) {
            throw new IllegalStateException("MASTER_KEY not found.");
        }

        String key = NWE_ENCRYPTED_KEY_BASE64;
        if (fallback) {
            key = FALLBACK_ENCRYPTED_KEY_BASE64;
        }

        byte[] encryptedKeyBytes = Base64.getDecoder().decode(key);
        byte[] masterKeyBytes = masterKey.getBytes(StandardCharsets.UTF_8);

        SecretKeySpec secretKey = new SecretKeySpec(masterKeyBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(encryptedKeyBytes);

        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    public static String getDecryptedKey(boolean fallback) {
        String accountsKnsId = "NOT_DEFINED";
        try {
            accountsKnsId = getDecryptedKey(getMasterKey(), fallback);
        } catch (Exception e) {
            logger.error("lock-data-java -Master key not found. Ensure 'LOCK_DATA_KEY' is set in your .env file. {}", e.getMessage());
        }
        return accountsKnsId;
    }
}
