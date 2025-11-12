package com.inclusive.tenantservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.inclusive.tenantservice.model.TenantServiceApplication;

@Repository
public interface TenantServiceApplicationRepository extends JpaRepository<TenantServiceApplication, Long> {
}