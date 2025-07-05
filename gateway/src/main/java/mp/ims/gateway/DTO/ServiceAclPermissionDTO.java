package mp.ims.gateway.DTO;


public class ServiceAclPermissionDTO {
    private String serviceName;
    private Boolean canRead;
    private Boolean canWrite;

    public ServiceAclPermissionDTO(String serviceName, Boolean canRead, Boolean canWrite) {
        this.serviceName = serviceName;
        this.canRead = (canRead == null || canRead);
        this.canWrite = (canWrite == null || canWrite);
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
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
}
