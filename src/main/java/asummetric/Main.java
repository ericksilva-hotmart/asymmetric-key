package asummetric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args)  {
        LockData lockData = new LockData();
        String message = "https://app.localstack.cloud/inst/default/resources/kms";

        String encodedMessage = lockData.encrypt(message);
        logger.info("Mensagem criptografada enviada: {}", encodedMessage);

        logger.info("-------------------------------------------------------s---");
        String decryptedMessage = lockData.decrypt(encodedMessage);
        logger.info("\nMensagem recebida e descriptografada: {}", decryptedMessage);
    }
}
