package com.inclusive.authservice.service;

import com.inclusive.authservice.dto.UserAccountDTO;

import java.util.List;

/**
 * Service interface for UserAccount operations.
 */
public interface UserAccountService {

    List<UserAccountDTO> findAll();

    UserAccountDTO findById(Long id);

    UserAccountDTO create(UserAccountDTO dto);

    UserAccountDTO update(Long id, UserAccountDTO dto);

    void delete(Long id);
}
