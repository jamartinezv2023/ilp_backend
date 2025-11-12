package com.inclusive.authservice.service.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.inclusive.authservice.model.Institution;
import com.inclusive.authservice.repository.InstitutionRepository;
import com.inclusive.authservice.service.InstitutionService;

@Service
public class InstitutionServiceImpl implements InstitutionService {

    @Autowired
    private InstitutionRepository repository;

    @Override
    public List<Institution> listAll() { return repository.findAll(); }

    @Override
    public Institution getById(Long id) { return repository.findById(id).orElse(null); }

    @Override
    public Institution create(Institution entity) { return repository.save(entity); }

    @Override
    public Institution update(Long id, Institution entity) {
        Optional<Institution> opt = repository.findById(id);
        if (!opt.isPresent()) return null;
        entity.setId(id);
        return repository.save(entity);
    }

    @Override
    public void delete(Long id) { repository.deleteById(id); }
}