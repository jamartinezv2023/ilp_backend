package com.inclusive.learning.commons.controller;

import com.inclusive.learning.commons.dto.CommonResourceDto;
import com.inclusive.learning.commons.service.CommonResourceService;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/common/resources")
public class CommonResourceController {

    private final CommonResourceService service;

    public CommonResourceController(CommonResourceService service) {
        this.service = service;
    }

    @GetMapping
    public List<CommonResourceDto> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public CommonResourceDto findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public CommonResourceDto create(@RequestBody CommonResourceDto dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public CommonResourceDto update(
            @PathVariable Long id,
            @RequestBody CommonResourceDto dto
    ) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        return service.delete(id);
    }
}