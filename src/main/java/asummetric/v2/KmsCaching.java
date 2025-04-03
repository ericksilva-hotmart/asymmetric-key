package asummetric.v2;

import asummetric.config.KmsConfigure;
import com.amazonaws.encryptionsdk.AwsCrypto;
import com.amazonaws.encryptionsdk.CommitmentPolicy;
import com.amazonaws.encryptionsdk.CryptoMaterialsManager;
import com.amazonaws.encryptionsdk.caching.CachingCryptoMaterialsManager;
import com.amazonaws.encryptionsdk.caching.LocalCryptoMaterialsCache;
import com.amazonaws.encryptionsdk.kmssdkv2.KmsMasterKeyProvider;
import java.util.Base64;
import java.util.concurrent.TimeUnit;
import software.amazon.awssdk.services.kms.KmsClient;
import java.nio.charset.StandardCharsets;

public class KmsCaching {

    private final AwsCrypto crypto;
    private final CryptoMaterialsManager materialsManager;
    private final KmsClient kmsClient;

    public KmsCaching(String kmsKeyArn) {
        crypto = AwsCrypto.builder()
                .withCommitmentPolicy(CommitmentPolicy.RequireEncryptRequireDecrypt)
                .build();
        kmsClient = KmsConfigure.create();

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

    public String encryptData(String plaintext) {
        byte[] encrypted = crypto.encryptData(materialsManager, plaintext.getBytes(StandardCharsets.UTF_8))
                .getResult();
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public String decryptData(String ciphertext) {
        byte[] encryptedMessage = Base64.getDecoder().decode(ciphertext);
        byte[] decrypted = crypto.decryptData(materialsManager, encryptedMessage)
                .getResult();
        return new String(decrypted, StandardCharsets.UTF_8);
    }

}