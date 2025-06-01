package mp.ims.gateway.dao;

import mp.ims.gateway.models.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApiKeyDao extends JpaRepository<ApiKey, Integer> {
    Optional<ApiKey> findByKeyHash(String hash);
}
