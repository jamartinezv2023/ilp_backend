// Location: auth-service/src/main/java/com/inclusive/authservice/service/impl/UserAccountServiceImpl.java
package com.inclusive.authservice.service.impl;

import com.inclusive.authservice.dto.CreateUserAccountRequest;
import com.inclusive.authservice.dto.UpdateUserAccountRequest;
import com.inclusive.authservice.dto.UserAccountDTO;
import com.inclusive.authservice.entity.UserAccount;
import com.inclusive.authservice.mapper.UserAccountMapper;
import com.inclusive.authservice.repository.UserAccountRepository;
import com.inclusive.authservice.service.UserAccountService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserAccountServiceImpl implements UserAccountService {

    private final UserAccountRepository repository;
    private final UserAccountMapper mapper;
    private final PasswordEncoder passwordEncoder;

    public UserAccountServiceImpl(
            UserAccountRepository repository,
            UserAccountMapper mapper,
            PasswordEncoder passwordEncoder
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<UserAccountDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public UserAccountDTO findById(UUID id) {
        UserAccount entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("UserAccount not found"));
        return mapper.toDto(entity);
    }

    @Override
    public UserAccountDTO create(CreateUserAccountRequest request) {
        UserAccount entity = new UserAccount();

        entity.setTenantId(request.getTenantId());
        entity.setEmail(request.getEmail());
        entity.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        entity.setEnabled(true);

        return mapper.toDto(repository.save(entity));
    }

    @Override
    public UserAccountDTO update(UUID id, UpdateUserAccountRequest request) {
        UserAccount existing = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("UserAccount not found"));

        if (request.getEmail() != null) {
            existing.setEmail(request.getEmail());
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            existing.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        return mapper.toDto(repository.save(existing));
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}
