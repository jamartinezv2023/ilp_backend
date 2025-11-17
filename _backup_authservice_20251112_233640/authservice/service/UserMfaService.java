package com.inclusive.authservice.service;

import java.util.List;
import com.inclusive.authservice.model.UserMfa;

public interface UserMfaService {
    List<UserMfa> listAll();
    UserMfa getById(Long id);
    UserMfa create(UserMfa entity);
    UserMfa update(Long id, UserMfa entity);
    void delete(Long id);
}