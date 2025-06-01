package mp.ims.gateway.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import mp.ims.gateway.DTO.ServiceAclPermissionDTO;
import mp.ims.gateway.models.CustomUserDetails;
import mp.ims.gateway.models.User;
import org.springframework.beans.factory.annotation.Value;
import io.jsonwebtoken.Jwts.SIG;
import org.springframework.stereotype.Service;


import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String base64Secret;

    private SecretKey key;

    @PostConstruct
    public void init() {
        byte[] keyBytes = java.util.Base64.getDecoder().decode(base64Secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(User u, List<ServiceAclPermissionDTO> permissions) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date exp = new Date(nowMillis + 1000 * 60 * 60); // 1 hour

        return Jwts.builder()
                .subject(String.valueOf(u.getId()))
                .claims()
                    .add("org_id", u.getOrganization().getId())
                    .add("role", u.getRole())
                    .add("permissions", permissions)
                .and()
                .issuedAt(now)
                .expiration(exp)
                .signWith(this.key)
                .compact();
    }

    public Claims extractAllClaims(String jwt) {
        return Jwts.parser().verifyWith(this.key).build().parseSignedClaims(jwt).getPayload();
    }


    public boolean isTokenValid(String token, CustomUserDetails customUserDetails) {
        Claims claims = extractAllClaims(token);
        boolean isTokenExpired = claims.getExpiration().after(new Date());

        return String.valueOf(customUserDetails.getUserId()).equals(claims.getSubject()) && !isTokenExpired;
    }


}
