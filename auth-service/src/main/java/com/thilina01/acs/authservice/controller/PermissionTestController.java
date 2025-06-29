package com.thilina01.acs.authservice.controller;

import com.thilina01.acs.authservice.service.RedisPermissionService;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/permissions")
public class PermissionTestController {

    private final RedisPermissionService redisPermissionService;

    public PermissionTestController(RedisPermissionService redisPermissionService) {
        this.redisPermissionService = redisPermissionService;
    }

    @PostMapping("/grant")
    public String grant(@AuthenticationPrincipal Jwt jwt, @RequestBody Map<String, String> body) {
        String caller = jwt.getSubject();
        String targetUser = body.getOrDefault("targetUser", caller);
        String permission = body.get("permission");

        if (permission == null || permission.isBlank()) {
            throw new IllegalArgumentException("Missing 'permission' field in body");
        }

        // Ensure only admins can grant to others
        if (!targetUser.equals(caller) && !jwt.getClaimAsStringList("roles").contains("ROLE_ADMIN")) {
            throw new SecurityException("Only admins can grant permissions to other users");
        }

        redisPermissionService.grantTemporaryPermission(targetUser, permission, Duration.ofMinutes(10));
        return "Granted " + permission + " to " + targetUser + " for 10 minutes.";
    }

    @GetMapping("/list")
    public List<String> list(@AuthenticationPrincipal Jwt jwt) {
        return redisPermissionService.getPermissions(jwt.getSubject());
    }
}
