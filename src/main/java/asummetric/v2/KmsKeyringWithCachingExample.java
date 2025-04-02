package asummetric.v2;

import asummetric.config.KmsConfigure;
import com.amazonaws.encryptionsdk.AwsCrypto;
import com.amazonaws.encryptionsdk.CommitmentPolicy;
import com.amazonaws.encryptionsdk.CryptoMaterialsManager;
import com.amazonaws.encryptionsdk.caching.CachingCryptoMaterialsManager;
import com.amazonaws.encryptionsdk.caching.LocalCryptoMaterialsCache;
import com.amazonaws.encryptionsdk.kmssdkv2.KmsMasterKeyProvider;
import java.util.concurrent.TimeUnit;
import software.amazon.awssdk.services.kms.KmsClient;
import java.nio.charset.StandardCharsets;

public class KmsKeyringWithCachingExample {

    private final AwsCrypto crypto;
    private final CryptoMaterialsManager materialsManager;

    public KmsKeyringWithCachingExample(String kmsKeyArn) {
        // Initialize the AWS Encryption SDK client
        crypto = AwsCrypto.builder()
                .withCommitmentPolicy(CommitmentPolicy.RequireEncryptRequireDecrypt)
                .build();

        // Create KMS client with LocalStack configuration
        KmsClient kmsClient = KmsConfigure.create();

        // Create KMS Master Key Provider
        KmsMasterKeyProvider keyProvider = KmsMasterKeyProvider.builder()
                .customRegionalClientSupplier(region -> kmsClient)
                .buildStrict(kmsKeyArn);

        LocalCryptoMaterialsCache cache = new LocalCryptoMaterialsCache(100); // max entries
        materialsManager = CachingCryptoMaterialsManager.newBuilder()
                .withMasterKeyProvider(keyProvider)
                .withCache(cache)
                .withMaxAge(10, TimeUnit.MINUTES)
                .withMessageUseLimit(1000)
                .build();
    }

    public byte[] encryptData(String plaintext) {
        return crypto.encryptData(materialsManager, plaintext.getBytes(StandardCharsets.UTF_8)).getResult();
    }

    public String decryptData(byte[] ciphertext) {
        return new String(crypto.decryptData(materialsManager, ciphertext).getResult(), StandardCharsets.UTF_8);
    }

    public static void main(String[] args) {
        String kmsKeyArn = "arn:aws:kms:us-east-1:000000000000:key/85baa6f6-d420-4a28-855d-3f06ecebfd6f";

        KmsKeyringWithCachingExample example = new KmsKeyringWithCachingExample(kmsKeyArn);

        String original = "Sensitive data to protect";
        byte[] ciphertext = example.encryptData(original);
        String decrypted = example.decryptData(ciphertext);

        System.out.println("Original: " + original);
        System.out.println("Decrypted: " + decrypted);
    }
}