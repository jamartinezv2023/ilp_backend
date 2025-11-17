package com.inclusive.tenantservice.service.impl;

import com.inclusive.tenantservice.dto.TenantDTO;
import com.inclusive.tenantservice.entity.Tenant;
import com.inclusive.tenantservice.mapper.TenantMapper;
import com.inclusive.tenantservice.repository.TenantRepository;
import com.inclusive.tenantservice.service.TenantService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;

    public TenantServiceImpl(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TenantDTO> findAll() {
        return tenantRepository.findAll()
                .stream()
                .map(TenantMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TenantDTO findById(Long id) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tenant not found"));
        return TenantMapper.toDto(tenant);
    }

    @Override
    public TenantDTO create(TenantDTO dto) {
        if (dto.getCode() != null && tenantRepository.existsByCode(dto.getCode())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tenant code already exists");
        }
        Tenant tenant = TenantMapper.toNewEntity(dto);
        Tenant saved = tenantRepository.save(tenant);
        return TenantMapper.toDto(saved);
    }

    @Override
    public TenantDTO update(Long id, TenantDTO dto) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tenant not found"));
        if (dto.getCode() != null && !dto.getCode().equals(tenant.getCode())
                && tenantRepository.existsByCode(dto.getCode())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tenant code already exists");
        }
        TenantMapper.updateEntity(tenant, dto);
        Tenant saved = tenantRepository.save(tenant);
        return TenantMapper.toDto(saved);
    }

    @Override
    public void delete(Long id) {
        if (!tenantRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tenant not found");
        }
        tenantRepository.deleteById(id);
    }
}