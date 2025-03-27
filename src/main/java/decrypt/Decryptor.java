package decrypt;


import asummetric.LockData;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Decryptor {

    private static final Logger logger = LoggerFactory.getLogger(Decryptor.class);
    private static final LockData lockData = new  LockData();

    public static void main(String[] args) throws Exception {
        String encryptedMessage = "SajHidy4ucYJFOFQHItvhGpL0d3DknhRpWwaaAnrLw586RaZhC83vyQSo5eAhxIlO4mEvHaPmjYBZPQAvmTAgX1OXE2UcqjBko8x/FIUXLUOoU1akqY47vurLOsDOQMlqGMJq5HG7vBCH1ltMv+oIucpD8KSPGfI41akEZVk6iL3UKspHc+t1UtoBfbUfUHmk9dNJ5kj/gnRRzn0U9z4eQHF+pgQJTjNVe1i4ke7zSk87YxwU/xf2WrYGI08aRcKQKxoqrQS9JIth+uWdvIeTXg0yCfhCsfgGmdtYhYGvSmaXbVM+ahSPSliIiOl4ndTurw/Q/tnLc0cCIQT7BOiVA==";
        logger.info("Encrypted Text Length: {}", encryptedMessage.length());
        logger.info("Encrypted Text Base64 Validation: {}", isBase64Encoded(encryptedMessage));
       try {
           String decryptedMessage = lockData.decrypt(encryptedMessage);
           logger.info("Decrypted: {}", decryptedMessage);
       }
       catch (Exception ex){
           logger.error("Decrypt ", ex);
           String decryptedMessageV2 = lockData.decryptV2(encryptedMessage);
           logger.info("Decrypted V2: {}", decryptedMessageV2);
       }

    }

    public static boolean isBase64Encoded(String s) {
        try {
            Base64.getDecoder().decode(s);
            return true;
        } catch (IllegalArgumentException e) {
            return false; // Invalid Base64 string
        }
    }
}
