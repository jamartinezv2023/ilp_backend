package com.inclusive.authservice.controller;\r\nimport java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.inclusive.authservice.model.StudentBehaviorMetric;
import com.inclusive.authservice.service.StudentBehaviorMetricService;\r\n@RestController
@RequestMapping("/api/studentbehaviormetric")
public class StudentBehaviorMetricController {\r\n    @Autowired
    private StudentBehaviorMetricService service;\r\n    @GetMapping("/list")
    public ResponseEntity<List<StudentBehaviorMetric>> list() { return ResponseEntity.ok(service.listAll()); }\r\n    @GetMapping("/{id}")
    public ResponseEntity<StudentBehaviorMetric> get(@PathVariable Long id) { return ResponseEntity.ok(service.getById(id)); }\r\n    @PostMapping
    public ResponseEntity<StudentBehaviorMetric> create(@RequestBody StudentBehaviorMetric entity) { return ResponseEntity.ok(service.create(entity)); }\r\n    @PutMapping("/{id}")
    public ResponseEntity<StudentBehaviorMetric> update(@PathVariable Long id, @RequestBody StudentBehaviorMetric entity) { return ResponseEntity.ok(service.update(id, entity)); }\r\n    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
}