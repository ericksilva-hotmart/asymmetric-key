package asummetric.local;

import asummetric.v2.AES;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Lock {

    private static String accountsKnsId;
    private static final Logger logger = LoggerFactory.getLogger(Lock.class);

    static {
        try {
            accountsKnsId = KeyManager.getDecryptedKey();
        } catch (Exception e) {
            accountsKnsId = "NOT_DEFINED";
            logger.error("Master key not found. Ensure 'LOCK_DATA_KEY' is set in your .env file. {}", e.getMessage());
        }
    }

    //Exemplo
    public String unlockData(String encryptedJson) {
        return AES.decrypt(encryptedJson, accountsKnsId);
    }

    public String lockWithJson(String textPlain) {
        return AES.encrypt(textPlain, accountsKnsId);
    }

    public String encryptJson(String json) {
        return AES.encrypt(json, accountsKnsId);
    }
}
