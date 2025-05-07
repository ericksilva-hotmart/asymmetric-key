package asummetric.local;

import asummetric.v2.AES;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Lock {

    private static final String KEY_ID;
    private static final Logger logger = LoggerFactory.getLogger(Lock.class);

    static {
        KEY_ID = KeyManager.getDecryptedKey(false);
    }

    //Exemplo
    public String unlockData(String encryptedJson) {
        try {
           return AES.decrypt(encryptedJson, KEY_ID);
        }catch (Exception e){
            logger.error("lock-data-java - Fail to decrypt using the new key");
            String oldAccountsKnsId = KeyManager.getDecryptedKey(true);
            return AES.decrypt(encryptedJson, oldAccountsKnsId);
        }
    }

    public String lockWithJson(String textPlain) {
        try {
            return AES.encrypt(textPlain, KEY_ID);
        }catch (Exception e){
            logger.error("lock-data-java - Fail to encrypt using the new key");
            String oldAccountsKnsId = KeyManager.getDecryptedKey(true);
            return AES.encrypt(textPlain, oldAccountsKnsId);
        }
    }

    public String encryptJson(String json) {
        return AES.encrypt(json, KEY_ID);
    }
}
