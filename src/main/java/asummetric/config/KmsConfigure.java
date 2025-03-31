package asummetric.config;

import java.net.URI;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.kms.KmsClient;

public class KmsConfigure {

    private KmsConfigure() {}

    public static KmsClient  create() {
        String accessKey = "mock_secret_key";
        String secretKey = "mock_secret_key";

        StaticCredentialsProvider provider = StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey)
        );

        //  KmsClient.create();
        return KmsClient.builder()
                .credentialsProvider(provider)
                .endpointOverride(URI.create("http://localhost:4566"))
                .region(Region.US_EAST_1)
                .build();
    }
}
