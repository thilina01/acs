package com.thilina01.acs.reportservice.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReportController {

    @GetMapping("/public")
    public String publicInfo() {
        return "This is public.";
    }

    @GetMapping("/generateReport")
    public ResponseEntity<String> generateReport(Authentication auth) {
        System.out.println("Authenticated user: " + auth.getName());
        System.out.println("Authorities: " + auth.getAuthorities());
        return ResponseEntity.ok("Report generated!");
    }
}
