package com.thilina01.acs.reportservice.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
public class ReportController {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @GetMapping("/public")
    public String publicInfo() {
        return "This is public.";
    }

    @PreAuthorize("hasAuthority('PERM_GENERATE_REPORT')")
    @GetMapping("/generateReport")
    public ResponseEntity<String> generateReport(Authentication auth) {
        System.out.println("Authenticated user: " + auth.getName());
        System.out.println("Authorities: " + auth.getAuthorities());

        String username = auth.getName();
        String mobile = null;

        if (auth.getPrincipal() instanceof Jwt jwt) {
            mobile = jwt.getClaim("mobile");
            System.out.println("Mobile from JWT: " + mobile);
        }

        if (mobile == null || mobile.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Mobile number not found in token.");
        }

        String message = "Hi " + username + ", your report has been generated.";

        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> payload = Map.of(
                    "message", message,
                    "numbers", List.of(mobile)
            );
            String jsonPayload = mapper.writeValueAsString(payload);
            kafkaTemplate.send("report-generated", jsonPayload);

            return ResponseEntity.ok("Report generated and SMS event sent.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating report: " + e.getMessage());
        }
    }

    @GetMapping("/whoami")
    public Map<String, Object> whoAmI(@AuthenticationPrincipal Jwt jwt) {

        Map<String, Object> info = new HashMap<>();
        info.put("username", jwt.getSubject());
        info.put("email", jwt.getClaim("email"));
        info.put("tenant", jwt.getClaim("tenant"));
        info.put("fullName", jwt.getClaim("fullName"));
        info.put("roles", jwt.getClaim("roles"));

        return info;
    }

    @GetMapping("/abac/finance-data")
    @PreAuthorize("@abac.isDepartment(authentication, 'finance')")
    public String financeOnlyAccess() {
        return "Confidential finance report data.";
    }

    @GetMapping("/abac/finance-after-hours")
    @PreAuthorize("@abac.isFinanceAfterHours(authentication)")
    public String financeOnlyAfterHours() {
        return "After-hours finance access granted.";
    }
}
