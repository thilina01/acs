package com.thilina01.acs.reportservice.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component("abac")
public class AbacService {

    public boolean isDepartment(Authentication auth, String requiredDept) {
        if (auth.getPrincipal() instanceof Jwt jwt) {
            String department = jwt.getClaimAsString("department");
            return requiredDept.equalsIgnoreCase(department);
        }
        return false;
    }

    public boolean isAfterHours() {
        int hour = java.time.LocalTime.now().getHour();
        return (hour < 8 || hour >= 18); // 6pm to 8am
    }

    public boolean isFinanceAfterHours(Authentication auth) {
        return isDepartment(auth, "finance") && isAfterHours();
    }
}
