package mp.ims.gateway.services;

import jakarta.transaction.Transactional;
import mp.ims.gateway.DTO.AddUserRequestBody;
import mp.ims.gateway.DTO.ServiceAclPermissionDTO;
import mp.ims.gateway.dao.AclPermissionDao;
import mp.ims.gateway.dao.ApiKeyDao;
import mp.ims.gateway.dao.UserDao;
import mp.ims.gateway.DTO.UserPermissionDTO;
import mp.ims.gateway.models.AclPermission;
import mp.ims.gateway.models.ApiKey;
import mp.ims.gateway.models.Organization;
import mp.ims.gateway.models.User;
import mp.ims.gateway.utils.ApiKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserDao userDao;
    private final ApiKeyDao apiKeyDao;
    private final AclPermissionDao aclPermissionDao;
    private final ApiKeyUtil apiKeyUtil;

    @Autowired
    public UserService(UserDao userDao, ApiKeyDao apiKeyDao, AclPermissionDao aclPermissionDao, ApiKeyUtil apiKeyUtil) {
        this.userDao = userDao;
        this.apiKeyDao = apiKeyDao;
        this.aclPermissionDao = aclPermissionDao;
        this.apiKeyUtil = apiKeyUtil;
    }

    @Transactional
    public String insertUser(AddUserRequestBody addUserRequestBody){
        User user = new User(addUserRequestBody.getName(), addUserRequestBody.getEmail(), addUserRequestBody.getRole());

        Organization org = new Organization();
        org.setId(addUserRequestBody.getOrgId());
        user.setOrganization(org);

        user = userDao.save(user);

        // generate api key and set it to table
        String key = ApiKeyUtil.generateApiKey();
        String hashedKey = apiKeyUtil.generateHMAC_SHA256(key);

        ApiKey apiKey = new ApiKey();
        apiKey.setUser(user);
        apiKey.setKeyHash(hashedKey);


        apiKeyDao.save(apiKey);

        List<AclPermission> aclPermissions = new ArrayList<>();

        // fetch permissions and set in acl table
        for(var req : addUserRequestBody.getPermissions()){
            AclPermission aclPermission = new AclPermission();

            Long serviceId = req.keySet().iterator().next();

            aclPermission.setServiceId(serviceId);
            aclPermission.setUserId(user.getId());

            if(req.get(serviceId).equals("can_read")){
                aclPermission.setCanRead(true);
            }else if (req.get(serviceId).equals("can_read")){
                aclPermission.setCanWrite(true);
            }else continue;

            aclPermissions.add(aclPermission);
        }

        if(!aclPermissions.isEmpty()) aclPermissionDao.saveAll(aclPermissions);

        return key;
    }

    public Optional<Map<User, List<ServiceAclPermissionDTO>>> getUserWithPermissions(Long id){
        List<UserPermissionDTO> result = userDao.fetchUserWithPermissions(id);

        if(result.isEmpty()){
            return Optional.empty();
        }

        Map<User, List<ServiceAclPermissionDTO>> map = new HashMap<>();
        User user = new User(result.get(0).getId(), result.get(0).getName(), result.get(0).getEmail(), result.get(0).getRole());
        List<ServiceAclPermissionDTO> permissionList = new ArrayList<>();

        for(UserPermissionDTO dto: result){
            if(dto.getServiceName()!=null) permissionList.add(new ServiceAclPermissionDTO(dto.getServiceName(), dto.getCanRead(), dto.getCanWrite()));
        }

        map.put(user, permissionList);
        return Optional.of(map);
    }

}
