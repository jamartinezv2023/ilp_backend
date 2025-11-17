package com.inclusive.tenantservice.service;

import com.inclusive.tenantservice.dto.TenantDTO;

import java.util.List;

public interface TenantService {

    List<TenantDTO> findAll();

    TenantDTO findById(Long id);

    TenantDTO create(TenantDTO dto);

    TenantDTO update(Long id, TenantDTO dto);

    void delete(Long id);
}