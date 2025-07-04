package com.thilina01.acs.authservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;
    private String password;
    private String email;
    private String mobile;
    private String fullName;
    private String department;
    private String role;

    @Column(nullable = false)
    private boolean active = false;
    @Column(nullable = false)
    private boolean mobileVerified = false;
}
