package com.inclusive.monitoringservice.controller;

import com.inclusive.monitoringservice.dto.MonitoringEventDTO;
import com.inclusive.monitoringservice.service.MonitoringEventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/monitoring/events")
public class MonitoringEventController {

    private final MonitoringEventService service;

    public MonitoringEventController(MonitoringEventService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<MonitoringEventDTO>> getAllEvents() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MonitoringEventDTO> getEventById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<MonitoringEventDTO> createEvent(@RequestBody MonitoringEventDTO dto) {
        MonitoringEventDTO created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}