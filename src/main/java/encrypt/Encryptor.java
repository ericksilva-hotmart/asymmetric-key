package encrypt;

import asummetric.LockData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Encryptor {

    private static final Logger logger = LoggerFactory.getLogger(Encryptor.class);
    private static final LockData lockData = new  LockData();

    public static void main(String[] args) throws Exception {
        String message = "https://exempple.com";
        String encryptedMessage = lockData.encrypt(message);
        logger.info("Encrypted: {}", encryptedMessage);
    }

}