package com.thilina01.acs.authservice.dto;

public record UserRegistrationRequest(
    String username,
    String password,
    String email,
    String mobile,
    String fullName,
    String department,
    String role
) {}
