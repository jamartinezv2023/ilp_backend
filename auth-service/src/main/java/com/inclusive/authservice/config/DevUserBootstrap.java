// Location: auth-service/src/main/java/com/inclusive/authservice/config/DevUserBootstrap.java
package com.inclusive.authservice.config;

import com.inclusive.authservice.entity.UserAccount;
import com.inclusive.authservice.repository.authorization.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DevUserBootstrap implements CommandLineRunner {

    private static final UUID DEV_TENANT_ID =
            UUID.fromString("00000000-0000-0000-0000-000000000001");

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        String email = "admin@demo.com";

        if (userAccountRepository.existsByEmailAndTenantId(email, DEV_TENANT_ID)) {
            return;
        }

        UserAccount user = new UserAccount(
                UUID.randomUUID(),
                DEV_TENANT_ID,
                email,
                passwordEncoder.encode("Admin123*"),
                true,
                false,
                null,
                Instant.now(),
                null
        );

        userAccountRepository.save(user);

        System.out.println("[DEV BOOTSTRAP] Seed user created: " + email);
    }
}
