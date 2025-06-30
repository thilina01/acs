package com.thilina01.acs.authservice.controller;

import com.thilina01.acs.authservice.service.DbPermissionService;
import com.thilina01.acs.authservice.service.RedisPermissionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/permissions")
public class PermissionTestController {

    private final DbPermissionService dbPermissionService;
    private final RedisPermissionService redisPermissionService;

    public PermissionTestController(
            DbPermissionService dbPermissionService,
            RedisPermissionService redisPermissionService) {
        this.dbPermissionService = dbPermissionService;
        this.redisPermissionService = redisPermissionService;
    }

    /**
     * ✅ Grant a temporary permission using Redis.
     * Only admins can grant to other users.
     */
    @PostMapping("/grant")
    public String grant(@AuthenticationPrincipal Jwt jwt, @RequestBody Map<String, String> body) {
        String caller = jwt.getSubject();
        String targetUser = body.getOrDefault("targetUser", caller);
        String permission = body.get("permission");

        if (permission == null || permission.isBlank()) {
            throw new IllegalArgumentException("Missing 'permission' field in body");
        }

        if (!targetUser.equals(caller) && !jwt.getClaimAsStringList("roles").contains("ROLE_ADMIN")) {
            throw new SecurityException("Only admins can grant permissions to other users");
        }

        redisPermissionService.grantTemporaryPermission(targetUser, permission, Duration.ofMinutes(10));
        return "Granted " + permission + " to " + targetUser + " for 10 minutes.";
    }

    /**
     * ✅ Returns DB-stored permissions of the logged-in user.
     * (Optional: for future long-term policy decisions)
     */
    @GetMapping("/list")
    public List<String> list(@AuthenticationPrincipal Jwt jwt) {
        return dbPermissionService.getPermissions(jwt.getSubject());
    }

    /**
     * ✅ Admin-only view of Redis permissions for any user (for auditing or UI).
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/redis/list")
    public List<String> redisList(@RequestParam String username) {
        return redisPermissionService.getPermissions(username);
    }

    /**
     * ✅ Self-view of Redis temporary permissions (for debugging or UI).
     */
    @GetMapping("/redis/self")
    public List<String> redisSelf(@AuthenticationPrincipal Jwt jwt) {
        return redisPermissionService.getPermissions(jwt.getSubject());
    }
}
