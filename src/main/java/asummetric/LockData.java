package asummetric;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.CreateKeyRequest;
import software.amazon.awssdk.services.kms.model.CreateKeyResponse;
import software.amazon.awssdk.services.kms.model.DecryptRequest;
import software.amazon.awssdk.services.kms.model.DecryptResponse;
import software.amazon.awssdk.services.kms.model.GetPublicKeyRequest;
import software.amazon.awssdk.services.kms.model.GetPublicKeyResponse;
import software.amazon.awssdk.services.kms.model.KeySpec;
import software.amazon.awssdk.services.kms.model.ScheduleKeyDeletionRequest;
import software.amazon.awssdk.services.kms.model.ScheduleKeyDeletionResponse;


public class LockData {

    private static final String KEY_ARN = "arn:aws:kms:us-east-1:000000000000:key/aaa90e7c-9283-4d25-8705-b7fac14f8bd7";
    private static final String KEY_USAGE = "ENCRYPT_DECRYPT";
    private static final Logger logger = LoggerFactory.getLogger(LockData.class);
    private final KmsClient kmsClient;


    public LockData() {
        this.kmsClient = KmsConfigure.create();
    }

    public String createAsymmetricKey() {
        CreateKeyResponse response = kmsClient.createKey(
                CreateKeyRequest.builder()
                        .keyUsage(KEY_USAGE)
                        .keySpec(KeySpec.RSA_2048)
                        .build());

        String keyId = response.keyMetadata().keyId();
        logger.info("Created Key ID: {}", keyId);
        return keyId;
    }

    public void scheduleKeyDeletion(String keyId) {
        ScheduleKeyDeletionResponse response = kmsClient.scheduleKeyDeletion(
                ScheduleKeyDeletionRequest.builder()
                        .keyId(keyId)
                        .pendingWindowInDays(7)
                        .build()
        );
        logger.info("Key deletion scheduled id: {} response  {} in {} days", keyId, response, 7);
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

    public String encrypt(String plaintext) throws Exception {
//        String keyId = createAsymmetricKey();
        PublicKey publicKey = getPublicKey(KEY_ARN);
//        scheduleKeyDeletion(keyId);
        return encryptData(plaintext, publicKey);
    }

    private String encryptData(String plaintext, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public String decrypt(String encryptedText) {
        byte[] encryptedDataBytes = Base64.getDecoder().decode(encryptedText.trim());

        DecryptResponse response = kmsClient.decrypt(
                DecryptRequest.builder()
                        .keyId(KEY_ARN)
                        .ciphertextBlob(SdkBytes.fromByteArray(encryptedDataBytes))
                        .encryptionAlgorithm("RSAES_PKCS1_V1_5")
                        .build());

        return new String(response.plaintext().asByteArray());
    }

    public String decryptV2(String encryptedText) {
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText.trim());

        DecryptRequest decryptRequest = DecryptRequest.builder()
                .ciphertextBlob(SdkBytes.fromByteArray(encryptedBytes))
                .keyId(KEY_ARN)
                .build();

        DecryptResponse decryptResponse = kmsClient.decrypt(decryptRequest);
        return decryptResponse.plaintext().asUtf8String();
    }
}
