package com.inclusive.tenantservice.service;

import java.util.List;
import com.inclusive.tenantservice.model.TenantServiceApplication;

public interface TenantServiceApplicationService {
    List<TenantServiceApplication> listAll();
    TenantServiceApplication getById(Long id);
    TenantServiceApplication create(TenantServiceApplication entity);
    TenantServiceApplication update(Long id, TenantServiceApplication entity);
    void delete(Long id);
}