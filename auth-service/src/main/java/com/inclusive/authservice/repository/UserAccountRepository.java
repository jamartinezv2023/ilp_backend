// Location: auth-service/src/main/java/com/inclusive/authservice/repository/UserAccountRepository.java
package com.inclusive.authservice.repository;

import com.inclusive.authservice.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserAccountRepository extends JpaRepository<UserAccount, UUID> {
}
