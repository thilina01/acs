package com.thilina01.acs.reportservice.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReportController {

    @GetMapping("/public")
    public String publicInfo() {
        return "This is public.";
    }

    @PreAuthorize("hasAuthority('PERM_GENERATE_REPORT')")
    @GetMapping("/generateReport")
    public ResponseEntity<String> generateReport(Authentication auth) {
        System.out.println("Authenticated user: " + auth.getName());
        System.out.println("Authorities: " + auth.getAuthorities());
        return ResponseEntity.ok("Report generated!");
    }

    @GetMapping("/whoami")
    public Map<String, Object> whoAmI(@AuthenticationPrincipal Jwt jwt) {

        Map<String, Object> info = new HashMap<>();
        info.put("username", jwt.getSubject());
        info.put("email", jwt.getClaim("email"));
        info.put("tenant", jwt.getClaim("tenant"));
        info.put("fullName", jwt.getClaim("fullName"));
        info.put("roles", jwt.getClaim("roles"));

        return info;
    }

}
