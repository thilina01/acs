package com.thilina01.acs.authservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thilina01.acs.authservice.entity.User;
import com.thilina01.acs.authservice.repository.UserRepository;
import com.thilina01.acs.authservice.service.OtpService;

import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/verify-mobile")
public class MobileVerificationController {

    private final UserRepository userRepository;
    private final OtpService otpService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public MobileVerificationController(UserRepository userRepository,
            OtpService otpService,
            KafkaTemplate<String, String> kafkaTemplate) {
        this.userRepository = userRepository;
        this.otpService = otpService;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = new ObjectMapper();
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendOtp(@AuthenticationPrincipal Jwt jwt) {
        String username = jwt.getSubject();

        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null || user.getMobile() == null) {
            return ResponseEntity.badRequest().body("Invalid user or missing mobile number.");
        }

        // ✅ Skip if already verified
        if (user.isMobileVerified()) {
            return ResponseEntity.ok("Mobile number is already verified.");
        }

        String otp = otpService.generateAndSaveOtp(user.getMobile());
        String message = "Your verification code is: " + otp;

        try {
            String jsonPayload = objectMapper.writeValueAsString(
                    Map.of("message", message, "numbers", List.of(user.getMobile())));
            kafkaTemplate.send("report-generated", jsonPayload);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to send OTP SMS.");
        }

        return ResponseEntity.ok("OTP sent to " + user.getMobile());
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmOtp(@AuthenticationPrincipal Jwt jwt, @RequestBody OtpConfirmRequest request) {
        String username = jwt.getSubject();

        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null || user.getMobile() == null) {
            return ResponseEntity.badRequest().body("Invalid user.");
        }

        if (otpService.verifyOtp(user.getMobile(), request.otp())) {
            user.setMobileVerified(true);
            userRepository.save(user);
            otpService.invalidateOtp(user.getMobile());
            return ResponseEntity.ok("Mobile number verified successfully.");
        }

        return ResponseEntity.status(403).body("Invalid or expired OTP.");
    }

    public record OtpConfirmRequest(@NotBlank String otp) {
    }
}
