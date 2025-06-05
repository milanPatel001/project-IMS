package mp.ims.gateway.dao;

import mp.ims.gateway.models.ItemGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemGroupDao extends JpaRepository<ItemGroup,Long> {
}
