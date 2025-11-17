package com.inclusive.assessmentservice.controller;

import com.inclusive.assessmentservice.dto.AssessmentDTO;
import com.inclusive.assessmentservice.service.AssessmentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assessments")
public class AssessmentController {

    private final AssessmentService service;

    public AssessmentController(AssessmentService service) {
        this.service = service;
    }

    @GetMapping
    public List<AssessmentDTO> all() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public AssessmentDTO get(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public AssessmentDTO create(@RequestBody AssessmentDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public AssessmentDTO update(@PathVariable Long id, @RequestBody AssessmentDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}