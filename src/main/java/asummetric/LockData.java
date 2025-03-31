package asummetric;

import asummetric.config.KmsConfigure;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.DecryptRequest;
import software.amazon.awssdk.services.kms.model.EncryptRequest;


public class LockData {

    private static final String KEY_ARN = "arn:aws:kms:us-east-1:000000000000:key/845cfa9f-ebfc-4ba0-a29c-80c4748ca8f6";
    private static final String RSA = "RSAES_OAEP_SHA_256";
    private static final Logger logger = LoggerFactory.getLogger(LockData.class);
    private final KmsClient kmsClient;


    public LockData() {
        this.kmsClient = KmsConfigure.create();
    }


    public String encrypt(String message) {
        logger.info("=== Start Encrypt");
        EncryptRequest encryptRequest = EncryptRequest.builder()
                .keyId(KEY_ARN)
                .plaintext(SdkBytes.fromUtf8String(message))
                .encryptionAlgorithm(RSA)
                .build();

        byte[] encryptedMessage = kmsClient.encrypt(encryptRequest).ciphertextBlob().asByteArray();
        String encrypted = Base64.getEncoder().encodeToString(encryptedMessage);
        logger.info("===  Encrypted ->  {}", encrypted);
        return  encrypted;
    }


    public String decrypt(String encryptedText) {
        logger.info("=== Start Decrypt");
        byte[] encryptedMessage = Base64.getDecoder().decode(encryptedText);

        DecryptRequest decryptRequest = DecryptRequest.builder()
                .keyId(KEY_ARN)
                .ciphertextBlob(SdkBytes.fromByteArray(encryptedMessage))
                .encryptionAlgorithm(RSA)
                .build();

        String decryptMessage = kmsClient.decrypt(decryptRequest).plaintext().asUtf8String();
        logger.info("=== Decrypt -> {}", decryptMessage);
        return decryptMessage;
    }
}
