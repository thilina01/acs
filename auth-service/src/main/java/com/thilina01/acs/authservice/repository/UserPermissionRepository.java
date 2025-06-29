package com.thilina01.acs.authservice.repository;

import com.thilina01.acs.authservice.model.UserPermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface UserPermissionRepository extends JpaRepository<UserPermission, Long> {
    List<UserPermission> findByUsernameAndExpiresAtAfter(String username, Instant now);
}
