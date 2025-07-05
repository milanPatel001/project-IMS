package mp.ims.gateway.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.UUID;

@Component
public class ApiKeyUtil {

    @Value("${security.hmac.secret}")
    private String secret;

    public String generateHMAC_SHA256(String key) {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secretKey);
            byte[] hashBytes = sha256_HMAC.doFinal(key.getBytes());
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate HMAC SHA256", e);
        }
    }

    public boolean isValid(String apiKey, String storedHash) {
        String computedHash = generateHMAC_SHA256(apiKey);
        return computedHash.equals(storedHash);
    }

    public static String generateApiKey() {
        return UUID.randomUUID().toString();
    }

}
