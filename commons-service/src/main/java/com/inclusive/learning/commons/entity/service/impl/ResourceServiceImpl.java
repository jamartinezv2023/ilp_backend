package com.inclusive.learning.commons.entity.service.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.inclusive.learning.commons.entity.model.Resource;
    import com.inclusive.learning.commons.entity.repository.ResourceRepository;
import com.inclusive.learning.commons.entity.service.ResourceService;

@Service
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    private ResourceRepository repository;

    @Override
    public List<Resource> listAll() { return repository.findAll(); }

    @Override
    public Resource getById(Long id) { return repository.findById(id).orElse(null); }

    @Override
    public Resource create(Resource entity) { return repository.save(entity); }

    @Override
    public Resource update(Long id, Resource entity) {
        Optional<Resource> opt = repository.findById(id);
        if (!opt.isPresent()) return null;
        entity.setId(id);
        return repository.save(entity);
    }

    @Override
    public void delete(Long id) { repository.deleteById(id); }
}