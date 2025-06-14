package mp.ims.gateway.controllers;

import mp.ims.gateway.DTO.AddUserRequestBody;
import mp.ims.gateway.DTO.OrganizationRequestBody;
import mp.ims.gateway.kafka.KafkaService;
import mp.ims.gateway.models.CustomUserDetails;
import mp.ims.gateway.services.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/org")
public class OrganizationController {
    private final OrganizationService organizationService;
    private final RestTemplate restTemplate;

    @Autowired
    public OrganizationController(OrganizationService organizationService, RestTemplate restTemplate, KafkaService kafkaService) {
        this.organizationService = organizationService;
        this.restTemplate = restTemplate;
    }


    @PostMapping("/add")
    public ResponseEntity<?> addOrganization(@RequestBody OrganizationRequestBody organizationRequestBody){

        if(organizationRequestBody.getName()==null) return ResponseEntity.status(400).body("name field is missing!!");
        if(organizationRequestBody.getEmail()==null) return ResponseEntity.status(400).body("email field is missing!!");
        if(organizationRequestBody.getOrgName()==null) return ResponseEntity.status(400).body("orgName field is missing!!");
        if(organizationRequestBody.getDescription()==null) return ResponseEntity.status(400).body("description field is missing!!");

        String key = organizationService.insertOrg(organizationRequestBody);

        String discoveryUrl = "http://localhost:8000/restart";

        String response = restTemplate.getForObject(discoveryUrl, String.class);
        System.out.println(response);



        return ResponseEntity.ok(key);
    }

}
