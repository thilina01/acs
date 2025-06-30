package com.thilina01.acs.reportservice.config;

import com.thilina01.acs.reportservice.security.PermissionFetcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationConverter converter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(form -> form.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/public").permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(converter)));

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        String secret = "bXlzZWNyZXRrZXltYWtlc3VyZXRvaGF2ZXZhbGlkaW50ZXJ2YWw=";
        byte[] key = Base64.getDecoder().decode(secret);
        return NimbusJwtDecoder.withSecretKey(new SecretKeySpec(key, "HmacSHA256")).build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter(PermissionFetcher permissionFetcher) {
        JwtGrantedAuthoritiesConverter rolesConverter = new JwtGrantedAuthoritiesConverter();
        rolesConverter.setAuthoritiesClaimName("roles");
        rolesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            List<GrantedAuthority> authorities = new ArrayList<>(rolesConverter.convert(jwt));

            List<String> dynamicPerms = permissionFetcher.fetchPermissions(jwt);
            if (dynamicPerms != null) {
                dynamicPerms.stream()
                        .map(p -> new SimpleGrantedAuthority("PERM_" + p))
                        .forEach(authorities::add);
            }

            List<String> roles = jwt.getClaimAsStringList("roles");
            if (roles != null && roles.contains("ROLE_ADMIN")) {
                List<String> adminExtras = List.of("GENERATE_REPORT", "DELETE_USER", "ACCESS_AUDIT_LOGS");
                adminExtras.stream()
                        .map(p -> new SimpleGrantedAuthority("PERM_" + p))
                        .forEach(authorities::add);
            }

            return authorities;
        });

        return converter;
    }
}
