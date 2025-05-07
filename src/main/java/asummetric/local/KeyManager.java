package asummetric.local;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeyManager {

    private static final Logger logger = LoggerFactory.getLogger(KeyManager.class);

    private KeyManager() {
        //not constructor
    }
    private static final String FALLBACK_ENCRYPTED_KEY_BASE64 = "bwYgS3kFWvEw+OnH0o7wtwljLLm017a+OsqyMJe8lluOZ9Wp/6NFgfx175a92r47gKHM8XJk6li93HRv5Kl3HX3Lknpsr1eUaxCXfdSIqb07XA5QwzlzMYQtjJ+qTSuKtKpqqA9t4A=="; // Chave arn:..
    private static final String NWE_ENCRYPTED_KEY_BASE64 = "eInhTeEPjPfgNebAol2lPeBUPwOVV2U3JJVoMMT94Uqacb5edAQDENhA4XR3N3aIrdgsTgvQowgynOSO+fSSnGPVCstgAiqdy4urliPFwwWiGrGfMyqjksOk";
    private static final int GCM_IV_LENGTH = 12; // 12 bytes IV para GCM
    private static final int GCM_TAG_LENGTH = 128; // bits
    private static final String PART_KEY = "DRx9SZhe9lZvXYJH";

    static String getMasterKey() {
        return PART_KEY;
    }

    public static String getDecryptedKey(String masterKey, boolean fallback)
            throws NoSuchPaddingException,
            NoSuchAlgorithmException,
            IllegalBlockSizeException,
            BadPaddingException,
            InvalidAlgorithmParameterException,
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

        if (masterKeyBytes.length != 16 && masterKeyBytes.length != 24 && masterKeyBytes.length != 32) {
            throw new IllegalArgumentException("Invalid AES key length (must be 16, 24 or 32 bytes)");
        }

        // Extrair IV (12 bytes iniciais)
        byte[] iv = new byte[GCM_IV_LENGTH];
        System.arraycopy(encryptedKeyBytes, 0, iv, 0, GCM_IV_LENGTH);

        // Extrair ciphertext + tag (restante)
        int ciphertextLength = encryptedKeyBytes.length - GCM_IV_LENGTH;
        byte[] ciphertext = new byte[ciphertextLength];
        System.arraycopy(encryptedKeyBytes, GCM_IV_LENGTH, ciphertext, 0, ciphertextLength);

        SecretKeySpec secretKey = new SecretKeySpec(masterKeyBytes, "AES");
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);

        byte[] decryptedBytes = cipher.doFinal(ciphertext);

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
