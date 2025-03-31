package asummetric;

import asummetric.config.KmsConfigure;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.DecryptRequest;
import software.amazon.awssdk.services.kms.model.EncryptRequest;
import software.amazon.awssdk.services.kms.model.GetPublicKeyRequest;
import software.amazon.awssdk.services.kms.model.GetPublicKeyResponse;


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

    public PublicKey getPublicKey(String keyId) throws InvalidKeySpecException, NoSuchAlgorithmException {
        GetPublicKeyResponse response = kmsClient.getPublicKey(
                GetPublicKeyRequest.builder()
                        .keyId(keyId)
                        .build());

        byte[] keyBytes = response.publicKey().asByteArray();
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(new X509EncodedKeySpec(keyBytes));
    }
}
