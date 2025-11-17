package com.inclusive.authservice.service.impl;

import com.inclusive.authservice.dto.UserAccountDTO;
import com.inclusive.authservice.entity.UserAccount;
import com.inclusive.authservice.mapper.UserAccountMapper;
import com.inclusive.authservice.repository.UserAccountRepository;
import com.inclusive.authservice.service.UserAccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Default implementation of UserAccountService.
 */
@Service
@Transactional
public class UserAccountServiceImpl implements UserAccountService {

    private final UserAccountRepository repository;

    public UserAccountServiceImpl(UserAccountRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserAccountDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(UserAccountMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserAccountDTO findById(Long id) {
        UserAccount entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("UserAccount not found with id: " + id));
        return UserAccountMapper.toDto(entity);
    }

    @Override
    public UserAccountDTO create(UserAccountDTO dto) {
        UserAccount entity = UserAccountMapper.toEntity(dto);
        // Aqu?? deber??as aplicar hashing de password en un escenario real.
        if (dto.getPassword() != null) {
            entity.setPasswordHash(dto.getPassword());
        }
        UserAccount saved = repository.save(entity);
        return UserAccountMapper.toDto(saved);
    }

    @Override
    public UserAccountDTO update(Long id, UserAccountDTO dto) {
        UserAccount existing = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("UserAccount not found with id: " + id));
        UserAccountMapper.updateEntityFromDto(dto, existing);
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            existing.setPasswordHash(dto.getPassword());
        }
        UserAccount saved = repository.save(existing);
        return UserAccountMapper.toDto(saved);
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("UserAccount not found with id: " + id);
        }
        repository.deleteById(id);
    }
}
