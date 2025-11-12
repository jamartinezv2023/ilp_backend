package com.inclusive.learning.commons.entity.controller;

import org.springframework.web.bind.annotation.*;
import com.inclusive.learning.commons.entity.Resource;
import com.inclusive.learning.commons.entity.service.ResourceService;
import java.util.List;

@RestController
@RequestMapping(""/api/resource"")
public class ResourceController {
    private final ResourceService service;

    public ResourceController(ResourceService service) { this.service = service; }

    @GetMapping
    public List<Resource> getAll() { return service.findAll(); }

    @GetMapping(""/{id}"")
    public Resource getById(@PathVariable Long id) { return service.findById(id).orElse(null); }

    @PostMapping
    public Resource create(@RequestBody Resource obj) { return service.save(obj); }

    @PutMapping(""/{id}"")
    public Resource update(@PathVariable Long id, @RequestBody Resource obj) {
        obj.setId(id);
        return service.save(obj);
    }

    @DeleteMapping(""/{id}"")
    public void delete(@PathVariable Long id) { service.deleteById(id); }
}