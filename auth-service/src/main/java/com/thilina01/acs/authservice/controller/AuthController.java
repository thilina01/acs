package com.thilina01.acs.authservice.controller;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thilina01.acs.authservice.security.JwtService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String password = payload.get("password");

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        var user = (User) authentication.getPrincipal();
        var roles = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        String jwt = jwtService.generateToken(username, roles, "engineering");

        return ResponseEntity.ok(Map.of("token", jwt));
    }

    @GetMapping("/me")
    public Map<String, Object> me(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();

        return Map.of(
                "username", jwt.getSubject(),
                "roles", jwt.getClaim("roles"),
                "department", jwt.getClaim("department"),
                "issuedAt", jwt.getIssuedAt(),
                "expiresAt", jwt.getExpiresAt());
    }

}
