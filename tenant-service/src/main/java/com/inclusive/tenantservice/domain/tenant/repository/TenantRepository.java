package com.inclusive.tenantservice.domain.tenant.repository;

import com.inclusive.tenantservice.domain.tenant.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {
    boolean existsByCode(String code);
}