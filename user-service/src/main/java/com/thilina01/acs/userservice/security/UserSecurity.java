package com.thilina01.acs.userservice.security;

import com.thilina01.acs.userservice.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component("userSecurity")
public class UserSecurity {

    private final UserRepository repository;

    public UserSecurity(UserRepository repository) {
        this.repository = repository;
    }

    public boolean isSelf(Long id, String jwtSubject) {
        return repository.findByUsername(jwtSubject)
                .map(user -> user.getId().equals(id))
                .orElse(false);
    }
}
