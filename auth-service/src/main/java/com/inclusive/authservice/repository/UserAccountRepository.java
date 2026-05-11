package com.inclusive.authservice.repository;

import com.inclusive.authservice.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository("legacyUserAccountRepository")
public interface UserAccountRepository
        extends JpaRepository<UserAccount, UUID> {

    Optional<UserAccount> findByTenantIdAndEmail(
            UUID tenantId,
            String email
    );
}
