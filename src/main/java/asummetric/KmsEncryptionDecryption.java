package asummetric;

import asummetric.config.KmsConfigure;
import java.util.Base64;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kms.KmsClient;
import software.amazon.awssdk.services.kms.model.DecryptRequest;
import software.amazon.awssdk.services.kms.model.DecryptResponse;
import software.amazon.awssdk.services.kms.model.EncryptRequest;
import software.amazon.awssdk.services.kms.model.EncryptResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class KmsEncryptionDecryption {

    private static String LOCAL_ID = "69868d22-5483-4422-9dcf-c99fd5f046f3" ;
    private static String KEY_ID = "arn:aws:kms:us-east-1:000000000000:key/845cfa9f-ebfc-4ba0-a29c-80c4748ca8f6";
    public static void main(String[] args) {
        KmsClient kmsClient = KmsConfigure.create();
        String plaintext = "https://google.com2";
        // Define encryption context
        Map<String, String> encryptionContext = new HashMap<>();
        encryptionContext.put("Context", LOCAL_ID);
        encryptionContext.put("realm", "Teachable1");

        // Encrypt data
        byte[] encrypted = encryptData(kmsClient, KEY_ID, plaintext, encryptionContext);
        String encryptedData = Base64.getEncoder().encodeToString(encrypted);
        System.out.println("Encrypted Data: " + encryptedData);

        // Decrypt data
        String decryptedText = decryptData(kmsClient, encryptedData, encryptionContext);
        System.out.println("Decrypted Text: " + decryptedText);

    }

    public static byte[] encryptData(KmsClient kmsClient, String keyId, String plaintext, Map<String, String> encryptionContext) {
        EncryptRequest encryptRequest = EncryptRequest.builder()
                .keyId(keyId)
                .plaintext(SdkBytes.fromUtf8String(plaintext))
                .encryptionContext(encryptionContext)
                .build();

        EncryptResponse encryptResponse = kmsClient.encrypt(encryptRequest);
        return encryptResponse.ciphertextBlob().asByteArray();
    }

    public static String decryptData(KmsClient kmsClient, String encryptedData, Map<String, String> encryptionContext) {
        byte[] encryptedMessage = Base64.getDecoder().decode(encryptedData);
        DecryptRequest decryptRequest = DecryptRequest.builder()
                .ciphertextBlob(SdkBytes.fromByteArray(encryptedMessage))
                .encryptionContext(encryptionContext)
                .build();

        DecryptResponse decryptResponse = kmsClient.decrypt(decryptRequest);
        return decryptResponse.plaintext().asUtf8String();
    }
}
