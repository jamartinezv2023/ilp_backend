package com.inclusive.authservice.service;

import java.util.List;
import com.inclusive.authservice.model.Role;

public interface RoleService {
    List<Role> listAll();
    Role getById(Long id);
    Role create(Role entity);
    Role update(Long id, Role entity);
    void delete(Long id);
}