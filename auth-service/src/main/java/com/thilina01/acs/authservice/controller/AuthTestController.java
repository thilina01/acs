package com.thilina01.acs.authservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthTestController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello from Auth Service!";
    }
}