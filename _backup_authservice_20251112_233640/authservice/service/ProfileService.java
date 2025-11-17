package com.inclusive.authservice.service;

import java.util.List;
import com.inclusive.authservice.model.Profile;

public interface ProfileService {
    List<Profile> listAll();
    Profile getById(Long id);
    Profile create(Profile entity);
    Profile update(Long id, Profile entity);
    void delete(Long id);
}