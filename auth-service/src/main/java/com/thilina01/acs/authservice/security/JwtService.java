package com.thilina01.acs.authservice.security;

import com.thilina01.acs.authservice.service.RedisPermissionService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    private static final String SECRET_KEY = "bXlzZWNyZXRrZXltYWtlc3VyZXRvaGF2ZXZhbGlkaW50ZXJ2YWw=";

    private final RedisPermissionService redisPermissionService;

    public JwtService(RedisPermissionService redisPermissionService) {
        this.redisPermissionService = redisPermissionService;
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String username, List<String> roles) {
        List<String> permissions = redisPermissionService.getPermissions(username);

        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .claim("permissions", permissions) // ✅ Add temp permissions here
                .claim("department", "engineering")
                .claim("tenant", "thilina01")
                .claim("email", username + "@thilina01.com")
                .claim("fullName", "Test Full Name")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

}
