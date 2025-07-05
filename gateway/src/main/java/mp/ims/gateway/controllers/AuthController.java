package mp.ims.gateway.controllers;

import mp.ims.gateway.DTO.ServiceAclPermissionDTO;
import mp.ims.gateway.models.User;
import mp.ims.gateway.services.ApiKeyService;
import mp.ims.gateway.services.JwtService;
import mp.ims.gateway.services.UserService;
import mp.ims.gateway.utils.ApiKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final ApiKeyUtil apiKeyUtil;
    private final ApiKeyService apiKeyService;
    private final UserService userService;
    private final JwtService jwtService;

    @Autowired
    public AuthController(ApiKeyUtil apiKeyUtil, ApiKeyService apiKeyService, UserService userService, JwtService jwtService) {
        this.apiKeyUtil = apiKeyUtil;
        this.apiKeyService = apiKeyService;
        this.userService = userService;
        this.jwtService = jwtService;
    }


    @GetMapping
    public ResponseEntity<?> authenticate(@RequestHeader(value = "X-API-KEY", required = false) String key) {

        if(key==null || key.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("X-API-KEY header is missing!!");

        String hash = apiKeyUtil.generateHMAC_SHA256(key);

        Optional<User> u = apiKeyService.getUserByApiKey(hash);
        if(u.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid API Key or API key has been revoked or expired !!!");

        var opt = userService.getUserWithPermissions(u.get().getId());
        //if(opt.isEmpty()) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid API Key");

        List<ServiceAclPermissionDTO> val = null;

        if(!opt.get().isEmpty()){
            var entry = opt.get().entrySet().iterator().next();
            val = entry.getValue();
        }

        String token = jwtService.generateToken(u.get(), val);

        return ResponseEntity.ok(token);

    }


}
