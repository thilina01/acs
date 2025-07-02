package com.thilina01.acs.authservice.security;

import com.thilina01.acs.authservice.entity.User;
import com.thilina01.acs.authservice.repository.UserRepository;
import com.thilina01.acs.authservice.service.RedisPermissionService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    private final RedisPermissionService redisPermissionService;
    private final UserRepository userRepository;
    private final Key signingKey;

    public JwtService(
            RedisPermissionService redisPermissionService,
            UserRepository userRepository,
            @Value("${jwt.secret}") String secretKey) {
        this.redisPermissionService = redisPermissionService;
        this.userRepository = userRepository;
        this.signingKey = generateKey(secretKey);
    }

    private Key generateKey(String base64Secret) {
        byte[] keyBytes = Decoders.BASE64.decode(base64Secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String username, List<String> roles) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        List<String> permissions = redisPermissionService.getPermissions(username);

        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("roles", roles)
                .claim("permissions", permissions)
                .claim("department", user.getDepartment())
                .claim("tenant", "thilina01")
                .claim("email", user.getEmail())
                .claim("mobile", user.getMobile())
                .claim("fullName", user.getFullName())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }
}
