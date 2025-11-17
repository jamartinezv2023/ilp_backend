package com.inclusive.authservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.inclusive.authservice.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}