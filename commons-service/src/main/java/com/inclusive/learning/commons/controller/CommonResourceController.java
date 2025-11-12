package com.inclusive.learning.commons.controller;

import com.inclusive.learning.commons.dto.CommonResourceDto;
import com.inclusive.learning.commons.service.CommonResourceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/commons")
public class CommonResourceController {

    private final CommonResourceService service;

    public CommonResourceController(CommonResourceService service) {
        this.service = service;
    }

    @GetMapping
    public List<CommonResourceDto> list() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResourceDto> get(@PathVariable Long id) {
        CommonResourceDto dto = service.findById(id);
        return dto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<CommonResourceDto> create(@Valid @RequestBody CommonResourceDto dto) {
        CommonResourceDto created = service.create(dto);
        return ResponseEntity.created(URI.create("/api/commons/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommonResourceDto> update(@PathVariable Long id, @Valid @RequestBody CommonResourceDto dto) {
        CommonResourceDto updated = service.update(id, dto);
        return updated == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean ok = service.delete(id);
        return ok ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}







