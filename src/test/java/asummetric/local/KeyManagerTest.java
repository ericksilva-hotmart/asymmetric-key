package asummetric.local;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;


class KeyManagerTest {

    @Test
    void testGetDecryptedKeyWithInjectedMasterKey() throws Exception {
        String masterKey = "DRx9SZhe9lZvXYJH";

        String decryptedKey = KeyManager.getDecryptedKey(masterKey);

        assertNotNull(decryptedKey);
        assertFalse(decryptedKey.isEmpty());
        assertEquals("arn:aws:kms:us-east-1", decryptedKey.substring(0, 21));
    }

    @Test
    void testGetDecryptedKeyWithoutEnvVariable() {
        Executable executable = KeyManager::getDecryptedKey;

        IllegalStateException exception = assertThrows(IllegalStateException.class, executable);

        assertEquals("MASTER_KEY not found.", exception.getMessage());
    }
}