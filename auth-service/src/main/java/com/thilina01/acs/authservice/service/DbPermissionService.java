package com.thilina01.acs.authservice.service;

import com.thilina01.acs.authservice.model.UserPermission;
import com.thilina01.acs.authservice.repository.UserPermissionRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class DbPermissionService {

    private final UserPermissionRepository repository;

    public DbPermissionService(UserPermissionRepository repository) {
        this.repository = repository;
    }

    public void grantTemporaryPermission(String username, String permission, Duration duration) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(duration);
        repository.save(new UserPermission(username, permission, now, expiresAt));
    }

    public List<String> getPermissions(String username) {
        return repository.findByUsernameAndExpiresAtAfter(username, Instant.now())
                         .stream()
                         .map(UserPermission::getPermission)
                         .toList();
    }
}
