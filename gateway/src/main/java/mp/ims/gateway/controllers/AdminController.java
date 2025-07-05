package mp.ims.gateway.controllers;

import mp.ims.gateway.DTO.AddUserRequestBody;
import mp.ims.gateway.models.CustomUserDetails;
import mp.ims.gateway.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/addMembers")
    public ResponseEntity<?> addOrganizationMembers(@RequestBody AddUserRequestBody addUserRequestBody){

        if(addUserRequestBody.getName()==null) return ResponseEntity.status(400).body("name field is missing!!");
        if(addUserRequestBody.getEmail()==null) return ResponseEntity.status(400).body("email field is missing!!");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        CustomUserDetails c = (CustomUserDetails) authentication.getPrincipal();
        addUserRequestBody.setOrgId(c.getOrgId());

        String key = userService.insertUser(addUserRequestBody);

        return ResponseEntity.ok(key);
    }
}
