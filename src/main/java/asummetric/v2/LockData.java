package asummetric.v2;

import asummetric.config.ToggleConfig;
import io.getunleash.Unleash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LockData {

    private final String kmsKeyArn;
    private final Unleash unleash;
    private final KmsCaching kmsCaching;
    private static final Logger logger = LoggerFactory.getLogger(LockData.class);

    public LockData(String kmsKeyArn) {
        this.kmsKeyArn = kmsKeyArn;
        this.unleash = ToggleConfig.toggleConfig();
        this.kmsCaching = new KmsCaching(kmsKeyArn);
    }

    public String encrypt(String plainText) {
        if (kmsEnable()){
            return this.kmsCaching.encryptData(plainText);
        }
        logger.info("Encrypt local");
        return AES.encrypt(plainText, kmsKeyArn);
    }

    public String decrypt(String plainText) {
        if (kmsEnable()){
            return this.kmsCaching.decryptData(plainText);
        }
        logger.info("Decrypt local");
        return AES.decrypt(plainText, kmsKeyArn);
    }

    public boolean kmsEnable() {
        return true;
    }

    public boolean kmsEnableV2() {
        return unleash != null && unleash.isEnabled("YOUR_TOGGLE_NAME");
    }

}
