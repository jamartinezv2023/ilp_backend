// Location: auth-service/src/main/java/com/inclusive/authservice/repository/authorization/UserAccountRepository.java
package com.inclusive.authservice.repository.authorization;

import com.inclusive.authservice.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserAccountRepository extends JpaRepository<UserAccount, UUID> {

    Optional<UserAccount> findByTenantIdAndEmail(String tenantId, String email);
}
