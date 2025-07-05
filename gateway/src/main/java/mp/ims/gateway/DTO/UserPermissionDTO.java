package mp.ims.gateway.DTO;

import java.util.List;

public class UserPermissionDTO {
    private Long id;
    private String name;
    private String email;
    private String role;
    private String serviceName;
    private Boolean canRead;
    private Boolean canWrite;

    public UserPermissionDTO(Long id, String name, String email, String role, String serviceName, Boolean canRead, Boolean canWrite) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.serviceName = serviceName;
        this.canRead = canRead;
        this.canWrite = canWrite;
    }


    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getServiceName() {
        return serviceName;
    }

    public Boolean getCanRead() {
        return canRead;
    }

    public Boolean getCanWrite() {
        return canWrite;
    }
}
