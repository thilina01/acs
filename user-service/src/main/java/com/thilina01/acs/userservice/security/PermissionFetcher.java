// PermissionFetcher.java
package com.thilina01.acs.userservice.security;

import com.thilina01.acs.userservice.config.AuthServiceProperties;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

@Component
public class PermissionFetcher {

    private final RestTemplate restTemplate = new RestTemplate();
    private final AuthServiceProperties authProps;

    public PermissionFetcher(@Qualifier("authServiceProperties") AuthServiceProperties authProps) {
        this.authProps = authProps;
    }

    public List<String> fetchPermissions(Jwt jwt) {
        try {
            String url = authProps.getUrl() + "/permissions/redis/self";

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(jwt.getTokenValue());

            RequestEntity<Void> request = RequestEntity
                    .get(URI.create(url))
                    .headers(headers)
                    .build();

            ResponseEntity<List> response = restTemplate.exchange(request, List.class);
            return response.getBody();
        } catch (Exception e) {
            // 🛡️ Log and fallback gracefully
            System.err.println("Warning: Failed to fetch dynamic permissions: " + e.getMessage());
            return List.of(); // fallback to empty
        }
    }
}
