package com.inclusive.authservice.repository.authorization;

import com.inclusive.authservice.entity.authorization.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {

    boolean existsByUserIdAndRoleIdAndTenantIdAndActiveTrue(
            UUID userId,
            UUID roleId,
            UUID tenantId
    );

    Optional<UserRole> findByUserIdAndRoleIdAndTenantIdAndActiveTrue(
            UUID userId,
            UUID roleId,
            UUID tenantId
    );

    @Query("""
        select ur.roleId
        from UserRole ur
        where ur.userId = :userId
          and ur.tenantId = :tenantId
          and ur.active = true
    """)
    List<UUID> findActiveRoleIdsByUserAndTenant(
            UUID userId,
            UUID tenantId
    );
}
