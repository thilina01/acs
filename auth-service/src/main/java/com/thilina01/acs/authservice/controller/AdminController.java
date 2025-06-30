package com.thilina01.acs.authservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thilina01.acs.authservice.repository.UserRepository;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserRepository userRepository;

    public AdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/activate/{username}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> activateUser(@PathVariable String username) {
        return userRepository.findByUsername(username)
                .map(user -> {
                    user.setActive(true);
                    userRepository.save(user);
                    return ResponseEntity.ok("User activated.");
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/deactivate/{username}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> deactivateUser(@PathVariable String username) {
        return userRepository.findByUsername(username)
                .map(user -> {
                    user.setActive(false);
                    userRepository.save(user);
                    return ResponseEntity.ok("User deactivated.");
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
