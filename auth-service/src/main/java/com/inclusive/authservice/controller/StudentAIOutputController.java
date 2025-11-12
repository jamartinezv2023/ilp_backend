package com.inclusive.authservice.controller;\r\nimport java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.inclusive.authservice.model.StudentAIOutput;
import com.inclusive.authservice.service.StudentAIOutputService;\r\n@RestController
@RequestMapping("/api/studentaioutput")
public class StudentAIOutputController {\r\n    @Autowired
    private StudentAIOutputService service;\r\n    @GetMapping("/list")
    public ResponseEntity<List<StudentAIOutput>> list() { return ResponseEntity.ok(service.listAll()); }\r\n    @GetMapping("/{id}")
    public ResponseEntity<StudentAIOutput> get(@PathVariable Long id) { return ResponseEntity.ok(service.getById(id)); }\r\n    @PostMapping
    public ResponseEntity<StudentAIOutput> create(@RequestBody StudentAIOutput entity) { return ResponseEntity.ok(service.create(entity)); }\r\n    @PutMapping("/{id}")
    public ResponseEntity<StudentAIOutput> update(@PathVariable Long id, @RequestBody StudentAIOutput entity) { return ResponseEntity.ok(service.update(id, entity)); }\r\n    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
}