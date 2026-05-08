package com.inclusive.learning.commons.entity.controller;

import com.inclusive.learning.commons.entity.model.Resource;
import com.inclusive.learning.commons.entity.service.ResourceService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/resources")
public class ResourceController {

    private final ResourceService service;

    public ResourceController(ResourceService service) {
        this.service = service;
    }

    @GetMapping
    public List<Resource> listAll() {
        return service.listAll();
    }
}