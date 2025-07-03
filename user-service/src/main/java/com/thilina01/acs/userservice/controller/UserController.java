package com.thilina01.acs.userservice.controller;

import com.thilina01.acs.userservice.model.User;
import com.thilina01.acs.userservice.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping
    public List<User> listAll() {
        return userService.findAll();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN') or @userSecurity.isSelf(#id, #jwt.subject)")
    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN') or @userSecurity.isSelf(#id, #jwt.subject)")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody User updated,
            @AuthenticationPrincipal Jwt jwt) {
        return userService.findById(id).map(existing -> {
            existing.setFullName(updated.getFullName());
            existing.setEmail(updated.getEmail());
            existing.setDepartment(updated.getDepartment());

            // 🔁 Check if mobile number was changed
            if (updated.getMobile() != null && !updated.getMobile().equals(existing.getMobile())) {
                existing.setMobile(updated.getMobile());
                existing.setMobileVerified(false); // ⛔ Reset verification
            }
                return ResponseEntity.ok(userService
            }).orElse(ResponseEntity.notFound().build());
                
                
            Authorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.ok().build();
    }
}
