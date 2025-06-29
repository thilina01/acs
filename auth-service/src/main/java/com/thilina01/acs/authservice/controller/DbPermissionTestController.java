package com.thilina01.acs.authservice.controller;

import com.thilina01.acs.authservice.model.UserPermission;
import com.thilina01.acs.authservice.repository.UserPermissionRepository;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/permissions/db")
public class DbPermissionTestController {

    private final UserPermissionRepository repository;

    public DbPermissionTestController(UserPermissionRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/grant")
    public String grant(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String permission = payload.get("permission");

        Instant now = Instant.now();
        Instant expiry = now.plus(10, ChronoUnit.MINUTES);

        repository.save(new UserPermission(username, permission, now, expiry));

        return "Granted " + permission + " to " + username + " until " + expiry;
    }

    @GetMapping("/list")
    public List<String> list(@RequestParam String username) {
        return repository.findByUsernameAndExpiresAtAfter(username, Instant.now())
                         .stream()
                         .map(UserPermission::getPermission)
                         .toList();
    }
}
