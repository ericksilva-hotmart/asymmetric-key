package asummetric;

import asummetric.config.KmsConfigure;
import com.amazonaws.encryptionsdk.AwsCrypto;
import com.amazonaws.encryptionsdk.CommitmentPolicy;
import com.amazonaws.encryptionsdk.CryptoResult;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;
import software.amazon.cryptography.materialproviders.IKeyring;
import software.amazon.cryptography.materialproviders.MaterialProviders;
import software.amazon.cryptography.materialproviders.model.CreateAwsKmsKeyringInput;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import software.amazon.cryptography.materialproviders.model.MaterialProvidersConfig;


public class KmsEncryptionExample {
    private static String KEY_ARN = "arn:aws:kms:us-east-1:000000000000:key/845cfa9f-ebfc-4ba0-a29c-80c4748ca8f6";
    private static String LOCAL_ID = "69868d22-5483-4422-9dcf-c99fd5f046f3" ;
    
    public static void main(String[] args) {
        String plaintext = "Dados secretos";
        byte[] ciphertext = encryptData(plaintext);
        System.out.println("====================== Texto cipher: " + ciphertext);
        String decrypted = decryptData(ciphertext);
        System.out.println("====================== Texto descriptografado: " + decrypted);
    }

    private static byte[] encryptData(String plaintext) {

        AwsCrypto crypto = AwsCrypto.builder()
                .withCommitmentPolicy(CommitmentPolicy.RequireEncryptRequireDecrypt)
                .build();

        MaterialProvidersConfig config = MaterialProvidersConfig.builder().build();
        IKeyring kmsKeyring = MaterialProviders.builder()
                .MaterialProvidersConfig(config)
                .build()
                .CreateAwsKmsKeyring(
                        CreateAwsKmsKeyringInput.builder()
                        .kmsKeyId(KEY_ARN)
                        .kmsClient( KmsConfigure.create())
                        .build());

        Map<String, String> encryptionContext = Collections.singletonMap("id", LOCAL_ID);
        CryptoResult<byte[], ?> encryptResult = crypto.encryptData(kmsKeyring, plaintext.getBytes(StandardCharsets.UTF_8), encryptionContext);
        return encryptResult.getResult();
    }

    private static String decryptData(byte[] ciphertext) {
        AwsCrypto crypto = AwsCrypto.builder()
                .withCommitmentPolicy(CommitmentPolicy.RequireEncryptRequireDecrypt)
                .build();

        MaterialProvidersConfig config = MaterialProvidersConfig.builder().build();
        IKeyring kmsKeyring = MaterialProviders.builder()
                .MaterialProvidersConfig(config)
                .build()
                .CreateAwsKmsKeyring(
                        CreateAwsKmsKeyringInput.builder()
                                .kmsKeyId(KEY_ARN)
                                .kmsClient( KmsConfigure.create())
                                .build());

        Map<String, String> encryptionContext = Collections.singletonMap("id", LOCAL_ID);
        CryptoResult<byte[], ?> decryptResult = crypto.decryptData(kmsKeyring, ciphertext, encryptionContext);
        return new String(decryptResult.getResult(), StandardCharsets.UTF_8);
    }
}
