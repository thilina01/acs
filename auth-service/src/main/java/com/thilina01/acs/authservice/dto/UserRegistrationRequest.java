package com.thilina01.acs.authservice.dto;

public record UserRegistrationRequest(
    String username,
    String password,
    String email,
    String fullName,
    String department,
    String role
) {}
