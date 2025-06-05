package mp.ims.gateway.dao;

import mp.ims.gateway.models.AclCompositeKey;
import mp.ims.gateway.models.AclPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AclPermissionDao extends JpaRepository<AclPermission, AclCompositeKey> {
}
