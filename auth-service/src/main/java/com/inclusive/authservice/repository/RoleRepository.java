package com.inclusive.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.inclusive.authservice.model.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
}