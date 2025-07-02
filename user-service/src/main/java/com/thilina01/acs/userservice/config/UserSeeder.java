package com.thilina01.acs.userservice.config;

import com.thilina01.acs.userservice.model.User;
import com.thilina01.acs.userservice.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class UserSeeder {

    private final UserRepository repo;

    public UserSeeder(UserRepository repo) {
        this.repo = repo;
    }

    @PostConstruct
    public void init() {
        if (repo.count() == 0) {
            repo.save(User.builder()
                    .username("alice")
                    .fullName("Alice Jones")
                    .email("alice@example.com")
                    .mobile("+94724666666")
                    .role("USER")
                    .department("engineering")
                    .active(false)
                    .build());

            repo.save(User.builder()
                    .username("bob")
                    .fullName("Bob Smith")
                    .email("bob@example.com")
                    .mobile("+94776667121")
                    .role("MANAGER")
                    .department("finance")
                    .active(false)
                    .build());

            repo.save(User.builder()
                    .username("admin")
                    .fullName("Admin User")
                    .email("admin@example.com")
                    .mobile("+94776667121")
                    .role("ADMIN")
                    .department("it")
                    .active(true)
                    .build());
        }
    }
}
