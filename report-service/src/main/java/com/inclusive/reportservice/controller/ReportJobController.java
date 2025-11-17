package com.inclusive.reportservice.controller;

import com.inclusive.reportservice.dto.ReportJobDTO;
import com.inclusive.reportservice.service.ReportJobService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports/jobs")
public class ReportJobController {

    private final ReportJobService service;

    public ReportJobController(ReportJobService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<ReportJobDTO>> getAllJobs() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReportJobDTO> getJobById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<ReportJobDTO> createJob(@RequestBody ReportJobDTO dto) {
        ReportJobDTO created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReportJobDTO> updateJob(@PathVariable Long id, @RequestBody ReportJobDTO dto) {
        ReportJobDTO updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}