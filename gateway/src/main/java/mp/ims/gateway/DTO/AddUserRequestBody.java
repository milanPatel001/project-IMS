package mp.ims.gateway.DTO;

import java.util.List;
import java.util.Map;

public class AddUserRequestBody {
    private Long orgId;
    private String name;
    private String email;


    private String role;
    private List<Map<Long, String>> permissions; // <service_id, permission> Eg: <1, can_read>, <1, can_write>

    public AddUserRequestBody(String name, String email, String role, List<Map<Long, String>> permissions) {
        this.name = name;
        this.email = email;
        this.role = role;
        this.permissions = permissions;
    }

    public void setPermissions(List<Map<Long, String>> permissions) {
        this.permissions = permissions;
    }

    public List<Map<Long, String>> getPermissions() {
        return permissions;
    }

    public Long getOrgId() {
        return orgId;
    }

    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "AddUserRequestBody{" +
                "orgId=" + orgId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", permissions=" + permissions +
                '}';
    }
}
