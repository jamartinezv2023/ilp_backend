package com.inclusive.authservice.service;

import java.util.List;
import com.inclusive.authservice.model.Permission;

public interface PermissionService {
    List<Permission> listAll();
    Permission getById(Long id);
    Permission create(Permission entity);
    Permission update(Long id, Permission entity);
    void delete(Long id);
}