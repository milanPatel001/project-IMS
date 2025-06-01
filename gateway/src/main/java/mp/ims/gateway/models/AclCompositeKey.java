package mp.ims.gateway.models;


import java.io.Serializable;
import java.util.Objects;

public class AclCompositeKey implements Serializable {
    Integer userId;
    Integer serviceId;

    public AclCompositeKey() {
    }

    public AclCompositeKey(Integer userId, Integer serviceId) {
        this.userId = userId;
        this.serviceId = serviceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AclCompositeKey that = (AclCompositeKey) o;
        return Objects.equals(userId, that.userId) && Objects.equals(serviceId, that.serviceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, serviceId);
    }
}
