package com.inclusive.tenantservice.tenant.entity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.inclusive.tenantservice.tenant.entity.model.Tenant;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {
}