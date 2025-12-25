// Location: auth-service/src/main/java/com/inclusive/authservice/service/UserAccountService.java
package com.inclusive.authservice.service;

import com.inclusive.authservice.dto.CreateUserAccountRequest;
import com.inclusive.authservice.dto.UpdateUserAccountRequest;
import com.inclusive.authservice.dto.UserAccountDTO;

import java.util.List;
import java.util.UUID;

public interface UserAccountService {

    List<UserAccountDTO> findAll();

    UserAccountDTO findById(UUID id);

    UserAccountDTO create(CreateUserAccountRequest request);

    UserAccountDTO update(UUID id, UpdateUserAccountRequest request);

    void delete(UUID id);
}
