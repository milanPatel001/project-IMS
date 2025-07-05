package mp.ims.gateway.configs;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mp.ims.gateway.DTO.ServiceAclPermissionDTO;
import mp.ims.gateway.models.CustomUserDetails;
import mp.ims.gateway.services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Autowired
    public JWTAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String header = request.getHeader("Authorization");
        if (header==null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.split(" ")[1].trim();
        Claims claims;
        try {
            claims = jwtService.extractAllClaims(token);
        }catch (MalformedJwtException | ExpiredJwtException m){
            System.out.println(m.getMessage());
            filterChain.doFilter(request, response);
            return;
        }


        CustomUserDetails c = new CustomUserDetails(claims.get("org_id", Long.class), claims.getSubject(),(List<Map<String, Object>>) claims.get("permissions"), claims.get("role", String.class));

        if(SecurityContextHolder.getContext().getAuthentication()==null && jwtService.isTokenValid(token, c)){
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(c, null, c.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
            System.out.println("SUCCEEDED");
        }

        filterChain.doFilter(request, response);

    }
}
