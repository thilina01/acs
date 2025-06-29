package com.thilina01.acs.authservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.User;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean // 👈 CRUCIAL: this was missing in your version!
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/hello", "/auth/login").permitAll()
                        .anyRequest().authenticated())
                .httpBasic(customHttp -> customHttp.disable())
                .formLogin(form -> form.disable())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults())); // still needed
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

@Bean
public UserDetailsService userDetailsService(PasswordEncoder encoder) {
    return new InMemoryUserDetailsManager(
            User.withUsername("alice")
                    .password(encoder.encode("password123"))
                    .roles("USER")
                    .build(),

            User.withUsername("admin")
                    .password(encoder.encode("admin123"))
                    .roles("USER", "ADMIN")
                    .build()
    );
}


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        String secret = "bXlzZWNyZXRrZXltYWtlc3VyZXRvaGF2ZXZhbGlkaW50ZXJ2YWw="; // same as JwtService
        byte[] key = java.util.Base64.getDecoder().decode(secret);
        return NimbusJwtDecoder.withSecretKey(new javax.crypto.spec.SecretKeySpec(key, "HmacSHA256")).build();
    }

}
