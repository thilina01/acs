package com.thilina01.acs.authservice.controller;

import com.thilina01.acs.authservice.dto.AuthMeResponse;
import com.thilina01.acs.authservice.dto.UserRegistrationRequest;
import com.thilina01.acs.authservice.entity.User;
import com.thilina01.acs.authservice.repository.UserRepository;
import com.thilina01.acs.authservice.security.JwtService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        String username = payload.get("username");
        String password = payload.get("password");

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isActive()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Account is not activated yet."));
        }

        String jwt = jwtService.generateToken(username, roles);
        return ResponseEntity.ok(Map.of("token", jwt));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegistrationRequest request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists.");
        }

        User user = new User();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setEmail(request.email());
        user.setFullName(request.fullName());
        user.setDepartment(request.department());
        user.setRole(request.role());
        user.setActive(false); // 🔒 Mark as inactive until admin activates

        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully. Awaiting admin activation.");
    }

    @GetMapping("/me")
    public AuthMeResponse me(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();

        return new AuthMeResponse(
                jwt.getSubject(),
                getClaimSafe(jwt, "roles"),
                getClaimSafe(jwt, "department"),
                jwt.getIssuedAt(),
                jwt.getExpiresAt());
    }

    private String getClaimSafe(Jwt jwt, String claim) {
        Object val = jwt.getClaim(claim);
        return val != null ? val.toString() : null;
    }

}
