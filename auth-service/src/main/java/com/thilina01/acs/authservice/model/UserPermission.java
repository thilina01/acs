package com.thilina01.acs.authservice.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "user_permissions")
public class UserPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String permission;

    private Instant grantedAt;

    private Instant expiresAt;

    // Constructors
    public UserPermission() {}

    public UserPermission(String username, String permission, Instant grantedAt, Instant expiresAt) {
        this.username = username;
        this.permission = permission;
        this.grantedAt = grantedAt;
        this.expiresAt = expiresAt;
    }

    // Getters and Setters
    public Long getId() { return id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPermission() { return permission; }
    public void setPermission(String permission) { this.permission = permission; }

    public Instant getGrantedAt() { return grantedAt; }
    public void setGrantedAt(Instant grantedAt) { this.grantedAt = grantedAt; }

    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
}
