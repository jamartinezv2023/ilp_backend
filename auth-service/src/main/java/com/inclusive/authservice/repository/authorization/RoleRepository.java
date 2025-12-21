package com.inclusive.authservice.repository.authorization;

import com.inclusive.authservice.entity.authorization.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {

    boolean existsByTenantIdAndNameIgnoreCase(UUID tenantId, String name);

    List<Role> findAllByTenantIdAndActiveTrueAndDeletedAtIsNull(UUID tenantId);
}