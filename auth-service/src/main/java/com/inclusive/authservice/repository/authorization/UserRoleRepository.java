package com.inclusive.authservice.repository.authorization;

import com.inclusive.authservice.entity.authorization.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {

    @Query("""
        SELECT COUNT(ur) > 0
        FROM UserRole ur
        WHERE ur.userId = :userId
          AND ur.roleId = :roleId
    """)
    boolean existsByUserAndRole(
            @Param("userId") UUID userId,
            @Param("roleId") UUID roleId
    );

    @Query("""
        SELECT ur
        FROM UserRole ur
        WHERE ur.userId = :userId
          AND ur.roleId = :roleId
    """)
    Optional<UserRole> findByUserAndRole(
            @Param("userId") UUID userId,
            @Param("roleId") UUID roleId
    );
}