package com.thilina01.acs.userservice.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User identity and claims extracted from JWT")
public record WhoAmIResponse(
        @Schema(description = "Username from subject claim") String username,
        @Schema(description = "Granted roles from JWT") List<String> roles,
        @Schema(description = "User's email address") String email,
        @Schema(description = "User's mobile number") String mobile,
        @Schema(description = "User's department") String department,
        @Schema(description = "User's full name") String fullName
) {}