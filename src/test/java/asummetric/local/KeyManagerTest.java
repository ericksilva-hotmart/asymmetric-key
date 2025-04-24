package asummetric.local;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;


class KeyManagerTest {

    private static final String VALID_MASTER_KEY = "DRx9SZhe9lZvXYJH"; // 16 bytes para AES-128
    private static final String INVALID_MASTER_KEY = "invalidkey123456";

    @Test
    void testGetDecryptedKeyShouldThrowExceptionWhenMasterKeyIsNull() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            KeyManager.getDecryptedKey(null, false);
        });
        assertEquals("MASTER_KEY not found.", exception.getMessage());
    }

    @Test
    void testGetDecryptedKeyShouldThrowExceptionWhenMasterKeyInvalid() {
        assertThrows(Exception.class, () -> KeyManager.getDecryptedKey(INVALID_MASTER_KEY, false));
    }

    @Test
    void testGetDecryptedKeyWithInjectedMasterKey() throws Exception {
        String decryptedKey = KeyManager.getDecryptedKey(VALID_MASTER_KEY, false);

        assertNotNull(decryptedKey);
        assertFalse(decryptedKey.isEmpty());
        assertEquals("arn:aws:kms:us-east-1", decryptedKey.substring(0, 21));
        assertEquals("this-key-only-local-use", decryptedKey.substring(decryptedKey.length() -23));
    }

    @Test
    void testGetDecryptedKeyWithInjectedMasterKeyAndFallBack() throws Exception {
        String decryptedKey = KeyManager.getDecryptedKey(VALID_MASTER_KEY, true);

        assertNotNull(decryptedKey);
        assertFalse(decryptedKey.isEmpty());
        assertEquals("arn:aws:kms:us-east-1", decryptedKey.substring(0, 21));
    }
}