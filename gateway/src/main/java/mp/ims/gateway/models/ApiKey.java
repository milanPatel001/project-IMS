package mp.ims.gateway.models;

import jakarta.persistence.*;

import java.time.Duration;
import java.time.Instant;

@Entity
@Table(name = "api_keys")
public class ApiKey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean revoked;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant expiresAt;

    @Column(columnDefinition = "TEXT")
    private String keyHash;

    @OneToOne
    @JoinColumn(name = "user_id")
    User user;


    @PrePersist
    protected void onCreate() {
        revoked = false;
        createdAt = updatedAt = Instant.now();// UTC
        expiresAt = Instant.now().plus(Duration.ofDays(30));
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }



    public Long getId() {
        return id;
    }

    public Boolean getRevoked() {
        return revoked;
    }

    public void setRevoked(Boolean revoked) {
        this.revoked = revoked;
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

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public String getKeyHash() {
        return keyHash;
    }

    public void setKeyHash(String keyHash) {
        this.keyHash = keyHash;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
