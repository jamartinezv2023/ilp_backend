package com.inclusive.tenantservice.tenant.entity.service;

import java.util.List;
import com.inclusive.tenantservice.tenant.entity.model.Tenant;

public interface TenantService {
    List<Tenant> listAll();
    Tenant getById(Long id);
    Tenant create(Tenant entity);
    Tenant update(Long id, Tenant entity);
    void delete(Long id);
}