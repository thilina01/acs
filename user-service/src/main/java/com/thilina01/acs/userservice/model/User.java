package com.thilina01.acs.userservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    private String fullName;

    private String email;

    private String mobile;

    private String department;

    private String role; // USER, MANAGER, ADMIN

    @Column(nullable = false)
    private boolean active;
}
