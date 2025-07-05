package mp.ims.gateway.dao;

import mp.ims.gateway.models.User;
import mp.ims.gateway.DTO.UserPermissionDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDao extends JpaRepository<User, Long> {

    Optional<User> findByName(String name);

    Optional<User> findByEmail(String email);

    @Query(value = """
                        SELECT u.id, u.name, u.email, u.role,
                               m.name AS serviceName,
                               a.can_read,
                               a.can_write
                        FROM users u
                        LEFT JOIN acl_permissions a ON a.user_id = u.id
                        LEFT JOIN microservices m ON m.id = a.service_id
                        WHERE u.id = ?1
                    """, nativeQuery = true)
    List<UserPermissionDTO> fetchUserWithPermissions(Long id);
}
