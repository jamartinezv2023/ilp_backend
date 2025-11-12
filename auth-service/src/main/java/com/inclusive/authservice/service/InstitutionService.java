package com.inclusive.authservice.service;

import java.util.List;
import com.inclusive.authservice.model.Institution;

public interface InstitutionService {
    List<Institution> listAll();
    Institution getById(Long id);
    Institution create(Institution entity);
    Institution update(Long id, Institution entity);
    void delete(Long id);
}