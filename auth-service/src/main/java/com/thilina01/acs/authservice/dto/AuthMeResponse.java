package com.thilina01.acs.authservice.dto;

import java.time.Instant;

public record AuthMeResponse(
    String username,
    String roles,
    String department,
    Instant issuedAt,
    Instant expiresAt
) {}
