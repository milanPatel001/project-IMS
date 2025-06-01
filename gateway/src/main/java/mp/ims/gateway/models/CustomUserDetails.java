package mp.ims.gateway.models;

import mp.ims.gateway.DTO.ServiceAclPermissionDTO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

public class CustomUserDetails implements UserDetails {

    private final Long orgId;
    private final String userId;
    private final List<ServiceAclPermissionDTO> permissions;
    private final String role;

    public CustomUserDetails(Long orgId, String userId, List<Map<String, Object>> permissions, String role) {
        this.orgId = orgId;
        this.userId = userId;

        this.permissions =  permissions.stream()
                            .map(map -> new ServiceAclPermissionDTO(
                                    (String) map.get("service"),
                                    (Boolean) map.get("canRead"),
                                    (Boolean) map.get("canWrite")
                            ))
                            .toList();

        this.role = role;
    }

//    public CustomUserDetails(Long orgId, String userId, List<ServiceAclPermissionDTO> permissions, String role) {
//        this.orgId = orgId;
//        this.userId = userId;
//        this.permissions = permissions;
//        this.role = role;
//    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(role.equals("ADMIN")){
            List<GrantedAuthority> l = new ArrayList<>();
            l.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            return l;
        }

        return mapPermissionsToGrantedAuthority();
    }


    private List<SimpleGrantedAuthority> mapPermissionsToGrantedAuthority(){
        List<SimpleGrantedAuthority> l = new ArrayList<>();

        for(var p : this.permissions){
            String serviceName = p.getServiceName();

            if(p.getCanRead()){
                l.add(new SimpleGrantedAuthority(serviceName+":read"));
            }

            if(p.getCanWrite()){
                l.add(new SimpleGrantedAuthority(serviceName+":write"));
            }
        }

        return l;
    }

    public Long getOrgId() {
        return orgId;
    }

    public String getUserId() {
        return userId;
    }

    public List<ServiceAclPermissionDTO> getPermissions() {
        return permissions;
    }

    public String getRole() {
        return role;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
