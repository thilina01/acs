package com.thilina01.acs.userservice.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class ProfileController {

    @GetMapping("/whoami")
    public Map<String, Object> whoami(@AuthenticationPrincipal Jwt jwt) {
        
        return Map.of(
            "username", jwt.getSubject(),
            "roles", jwt.getClaimAsStringList("roles"),
            "email", jwt.getClaim("email"),
            "department", jwt.getClaim("department"),
            "fullName", jwt.getClaim("fullName")
        );
    }
}
