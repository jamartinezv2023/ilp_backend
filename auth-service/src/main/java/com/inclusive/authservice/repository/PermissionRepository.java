package com.inclusive.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.inclusive.authservice.model.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
}