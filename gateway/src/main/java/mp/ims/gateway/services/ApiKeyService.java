package mp.ims.gateway.services;

import mp.ims.gateway.dao.ApiKeyDao;
import mp.ims.gateway.models.ApiKey;
import mp.ims.gateway.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class ApiKeyService {
    private final ApiKeyDao apiKeyDao;

    @Autowired
    public ApiKeyService(ApiKeyDao apiKeyDao) {
        this.apiKeyDao = apiKeyDao;
    }

    public Optional<User> getUserByApiKey(String hash){

        Optional<ApiKey> apiKeyInfo = apiKeyDao.findByKeyHash(hash);
        System.out.println(apiKeyInfo.isEmpty() ? "Not found" : apiKeyInfo.get());
        if(apiKeyInfo.isEmpty() || apiKeyInfo.get().getRevoked() || isApiKeyExpired(apiKeyInfo.get().getExpiresAt())) return Optional.empty();

        return Optional.of(apiKeyInfo.get().getUser());
    }

    private boolean isApiKeyExpired(Instant expiry){
        return expiry.isBefore(Instant.now());
    }
}
