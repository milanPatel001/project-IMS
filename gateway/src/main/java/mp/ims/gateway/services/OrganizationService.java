package mp.ims.gateway.services;

import jakarta.transaction.Transactional;
import mp.ims.gateway.DTO.AddUserRequestBody;
import mp.ims.gateway.DTO.OrganizationRequestBody;
import mp.ims.gateway.dao.AclPermissionDao;
import mp.ims.gateway.dao.ApiKeyDao;
import mp.ims.gateway.dao.OrganizationDao;
import mp.ims.gateway.dao.UserDao;
import mp.ims.gateway.kafka.KafkaService;
import mp.ims.gateway.models.AclPermission;
import mp.ims.gateway.models.ApiKey;
import mp.ims.gateway.models.Organization;
import mp.ims.gateway.models.User;
import mp.ims.gateway.utils.ApiKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrganizationService {
    private final OrganizationDao organizationDao;
    private final UserDao userDao;
    private final ApiKeyDao apiKeyDao;
    private final ApiKeyUtil apiKeyUtil;
    private final KafkaService kafkaService;

    @Autowired
    public OrganizationService(OrganizationDao organizationDao, UserDao userDao, ApiKeyDao apiKeyDao, ApiKeyUtil apiKeyUtil, KafkaService kafkaService) {
        this.organizationDao = organizationDao;
        this.userDao = userDao;
        this.apiKeyDao = apiKeyDao;
        this.apiKeyUtil = apiKeyUtil;
        this.kafkaService = kafkaService;
    }

    @Transactional
    public String insertOrg(OrganizationRequestBody organizationRequestBody){
        Organization org = new Organization();
        org.setName(organizationRequestBody.getOrgName());
        org.setDescription(organizationRequestBody.getDescription());

        org = organizationDao.save(org);

        User user = new User(organizationRequestBody.getName(), organizationRequestBody.getEmail(), "ADMIN");
        user.setOrganization(org);

        user = userDao.save(user);

        String key = ApiKeyUtil.generateApiKey();
        String hashedKey = apiKeyUtil.generateHMAC_SHA256(key);

        ApiKey apiKey = new ApiKey();
        apiKey.setUser(user);
        apiKey.setKeyHash(hashedKey);

        apiKeyDao.save(apiKey);

        kafkaService.createTenantTopicsForOrg(org.getId());

        return key;
    }



}
