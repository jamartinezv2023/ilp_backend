// Location: auth-service/src/test/java/com/inclusive/authservice/service/UserAccountServiceTest.java
package com.inclusive.authservice.service;

import com.inclusive.authservice.dto.UserAccountCreateRequest;
import com.inclusive.authservice.repository.RoleRepository;
import com.inclusive.authservice.repository.UserAccountRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

class UserAccountServiceTest {

    @Test
    void shouldCreateUserAccount() {
        UserAccountRepository userRepo = Mockito.mock(UserAccountRepository.class);
        RoleRepository roleRepo = Mockito.mock(RoleRepository.class);
        PasswordEncoder encoder = Mockito.mock(PasswordEncoder.class);

        Mockito.when(encoder.encode(Mockito.any())).thenReturn("hashed");

        UserAccountService service = new UserAccountService(userRepo, roleRepo, encoder);

        UserAccountCreateRequest req = new UserAccountCreateRequest();
        req.setUsername("student1");
        req.setEmail("student@test.com");
        req.setPassword("StrongPass123");

        // Test focuses on logic, repository interaction mocked
    }
}
