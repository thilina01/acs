package com.thilina01.acs.authservice.security;

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

    private static final String SECRET_KEY = "bXlzZWNyZXRrZXltYWtlc3VyZXRvaGF2ZXZhbGlkaW50ZXJ2YWw="; // base64 256-bit

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String username, List<String> roles) {
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .claim("department", "engineering") // ✅ new claim
                .claim("tenant", "thilina01") // ✅ new claim
                .claim("email", username + "@thilina01.com") // ✅ new claim (can be dynamic)
                .claim("fullName", "Test Full Name") // ✅ new claim (could come from DB)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

}
