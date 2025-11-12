package com.inclusive.learning.commons.entity.service;

import java.util.List;
import com.inclusive.learning.commons.entity.model.Resource;

public interface ResourceService {
    List<Resource> listAll();
    Resource getById(Long id);
    Resource create(Resource entity);
    Resource update(Long id, Resource entity);
    void delete(Long id);
}