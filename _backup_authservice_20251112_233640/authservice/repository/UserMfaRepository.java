package com.inclusive.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.inclusive.authservice.model.UserMfa;

@Repository
public interface UserMfaRepository extends JpaRepository<UserMfa, Long> {
}