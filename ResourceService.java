package com.inclusive.learning.commons.service;

import com.inclusive.learning.commons.entity.Resource;
import com.inclusive.learning.commons.repository.ResourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ResourceService {

    @Autowired
    private ResourceRepository resourceRepository;

    public List<Resource> findAll() {
        return resourceRepository.findAll();
    }

    public Optional<Resource> findById(Long id) {
        return resourceRepository.findById(id);
    }

    public Resource save(Resource resource) {
        return resourceRepository.save(resource);
    }

    public void deleteById(Long id) {
        resourceRepository.deleteById(id);
    }

    public Resource update(Long id, Resource resourceDetails) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resource no encontrado con id " + id));
        resource.setName(resourceDetails.getName());
        resource.setDescription(resourceDetails.getDescription());
        return resourceRepository.save(resource);
    }
}




