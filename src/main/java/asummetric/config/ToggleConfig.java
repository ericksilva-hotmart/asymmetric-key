package asummetric.config;

import io.getunleash.DefaultUnleash;
import io.getunleash.Unleash;
import java.util.UUID;
import io.getunleash.util.UnleashConfig;

public class ToggleConfig {

    private static final String URL = "https://unleash.vulcano.rocks/api/";

    public static Unleash toggleConfig() {
        try {
            UnleashConfig.Builder builder = io.getunleash.util.UnleashConfig.builder()
                    .appName("APPLICATION-NAME")
                    .instanceId(UUID.randomUUID().toString())
                    .unleashAPI(URL);
            return new DefaultUnleash(builder.build());
        } catch (Exception ex){
            return null;
        }

    }
}
