package mp.ims.gateway.dao;

import mp.ims.gateway.models.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierDao extends JpaRepository<Supplier,Long> {
}
