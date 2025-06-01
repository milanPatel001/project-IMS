package mp.ims.gateway.models;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "acl_permissions")
@IdClass(AclCompositeKey.class)
public class AclPermission {

    @Id
    private Integer userId;

    @Id
    private Integer serviceId;

    private Boolean canRead;
    private Boolean canWrite;
    private Instant createdAt;
    private Instant updatedAt;


    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = Instant.now(); // UTC
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public Boolean getCanRead() {
        return canRead;
    }

    public void setCanRead(Boolean canRead) {
        this.canRead = canRead;
    }

    public Boolean getCanWrite() {
        return canWrite;
    }

    public void setCanWrite(Boolean canWrite) {
        this.canWrite = canWrite;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
