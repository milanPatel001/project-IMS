package mp.ims.gateway.services;

import mp.ims.gateway.DTO.ServiceAclPermissionDTO;
import mp.ims.gateway.dao.UserDao;
import mp.ims.gateway.DTO.UserPermissionDTO;
import mp.ims.gateway.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserDao userDao;

    @Autowired
    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public Optional<Map<User, List<ServiceAclPermissionDTO>>> getUserWithPermissions(Integer id){
        List<UserPermissionDTO> result = userDao.fetchUserWithPermissions(id);

        if(result.isEmpty()){
            return Optional.empty();
        }

        Map<User, List<ServiceAclPermissionDTO>> map = new HashMap<>();
        User user = new User(result.get(0).getId(), result.get(0).getName(), result.get(0).getEmail(), result.get(0).getRole());
        List<ServiceAclPermissionDTO> permissionList = new ArrayList<>();

        for(UserPermissionDTO dto: result){
            permissionList.add(new ServiceAclPermissionDTO(dto.getServiceName(), dto.getCanRead(), dto.getCanWrite()));
        }

        map.put(user, permissionList);
        return Optional.of(map);
    }

}
