package mp.ims.gateway.DTO;

public class OrganizationRequestBody {
    private String email;
    private String name;
    private String orgName;
    private String description;

    public OrganizationRequestBody() {
    }

    public OrganizationRequestBody(String email, String name, String orgName, String description) {
        this.email = email;
        this.name = name;
        this.orgName = orgName;
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
