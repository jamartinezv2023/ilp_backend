package com.inclusive.learning.commons.entity.service;

import org.springframework.stereotype.Service;
import com.inclusive.learning.commons.entity.Resource;
import com.inclusive.learning.commons.entity.repository.ResourceRepository;
import java.util.List;
import java.util.Optional;

@Service
public class ResourceService {
    private final ResourceRepository repository;

    public ResourceService(ResourceRepository repository) {
        this.repository = repository;
    }

    public List<Resource> findAll() { return repository.findAll(); }
    public Optional<Resource> findById(Long id) { return repository.findById(id); }
    public Resource save(Resource obj) { return repository.save(obj); }
    public void deleteById(Long id) { repository.deleteById(id); }
}