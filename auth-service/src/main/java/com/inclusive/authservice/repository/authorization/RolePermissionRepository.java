package com.inclusive.authservice.repository.authorization;

import com.inclusive.authservice.entity.authorization.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RolePermissionRepository extends JpaRepository<RolePermission, UUID> {

    boolean existsByRole_IdAndPermission_Id(UUID roleId, UUID permissionId);

    Optional<RolePermission> findByRole_IdAndPermission_Id(UUID roleId, UUID permissionId);

    @Query("""
        SELECT rp
        FROM RolePermission rp
        JOIN FETCH rp.role r
        JOIN FETCH rp.permission p
        WHERE r.id = :roleId
    """)
    List<RolePermission> findAllByRoleIdWithPermission(
            @Param("roleId") UUID roleId
    );
}