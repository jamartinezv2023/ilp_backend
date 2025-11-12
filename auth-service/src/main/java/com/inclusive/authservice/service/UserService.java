package com.inclusive.authservice.service;

import java.util.List;
import com.inclusive.authservice.model.User;

public interface UserService {
    List<User> listAll();
    User getById(Long id);
    User create(User entity);
    User update(Long id, User entity);
    void delete(Long id);
}