package com.inclusive.tenantservice.controller;\r\nimport java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.inclusive.tenantservice.model.TenantServiceApplication;
import com.inclusive.tenantservice.service.TenantServiceApplicationService;\r\n@RestController
@RequestMapping("/api/tenantserviceapplication")
public class TenantServiceApplicationController {\r\n    @Autowired
    private TenantServiceApplicationService service;\r\n    @GetMapping("/list")
    public ResponseEntity<List<TenantServiceApplication>> list() { return ResponseEntity.ok(service.listAll()); }\r\n    @GetMapping("/{id}")
    public ResponseEntity<TenantServiceApplication> get(@PathVariable Long id) { return ResponseEntity.ok(service.getById(id)); }\r\n    @PostMapping
    public ResponseEntity<TenantServiceApplication> create(@RequestBody TenantServiceApplication entity) { return ResponseEntity.ok(service.create(entity)); }\r\n    @PutMapping("/{id}")
    public ResponseEntity<TenantServiceApplication> update(@PathVariable Long id, @RequestBody TenantServiceApplication entity) { return ResponseEntity.ok(service.update(id, entity)); }\r\n    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
}