package com.thilina01.acs.userservice.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import com.thilina01.acs.userservice.dto.WhoAmIResponse;

@RestController
public class ProfileController {

    @GetMapping("/whoami")
    public WhoAmIResponse whoami(@AuthenticationPrincipal Jwt jwt) {
        return new WhoAmIResponse(
                jwt.getSubject(),
                jwt.getClaimAsStringList("roles"),
                getClaimSafe(jwt, "email"),
                getClaimSafe(jwt, "department"),
                getClaimSafe(jwt, "fullName")
        );
    }

    private String getClaimSafe(Jwt jwt, String claim) {
        Object value = jwt.getClaim(claim);
        return value != null ? value.toString() : null;
    }
}
