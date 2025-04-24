package asummetric.local;

import asummetric.v2.AES;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Lock {

    private static final String ACCOUNTS_KNS_ID;
    private static final Logger logger = LoggerFactory.getLogger(Lock.class);

    static {
        ACCOUNTS_KNS_ID = KeyManager.getDecryptedKey(false);
    }

    //Exemplo
    public String unlockData(String encryptedJson) {
        try {
           return AES.decrypt(encryptedJson, ACCOUNTS_KNS_ID);
        }catch (Exception e){
            logger.error("lock-data-java - Fail to decrypt using the new key");
            String oldAccountsKnsId = KeyManager.getDecryptedKey(true);
            return AES.decrypt(encryptedJson, oldAccountsKnsId);
        }
    }

    public String lockWithJson(String textPlain) {
        try {
            return AES.encrypt(textPlain, ACCOUNTS_KNS_ID);
        }catch (Exception e){
            logger.error("lock-data-java - Fail to encrypt using the new key");
            String oldAccountsKnsId = KeyManager.getDecryptedKey(true);
            return AES.encrypt(textPlain, oldAccountsKnsId);
        }
    }

    public String encryptJson(String json) {
        return AES.encrypt(json, ACCOUNTS_KNS_ID);
    }
}
