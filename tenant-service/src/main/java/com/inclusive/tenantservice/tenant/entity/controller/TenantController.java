package com.inclusive.tenantservice.tenant.entity.controller;\r\nimport java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.inclusive.tenantservice.tenant.entity.model.Tenant;
import com.inclusive.tenantservice.tenant.entity.service.TenantService;\r\n@RestController
@RequestMapping("/api/tenant")
public class TenantController {\r\n    @Autowired
    private TenantService service;\r\n    @GetMapping("/list")
    public ResponseEntity<List<Tenant>> list() { return ResponseEntity.ok(service.listAll()); }\r\n    @GetMapping("/{id}")
    public ResponseEntity<Tenant> get(@PathVariable Long id) { return ResponseEntity.ok(service.getById(id)); }\r\n    @PostMapping
    public ResponseEntity<Tenant> create(@RequestBody Tenant entity) { return ResponseEntity.ok(service.create(entity)); }\r\n    @PutMapping("/{id}")
    public ResponseEntity<Tenant> update(@PathVariable Long id, @RequestBody Tenant entity) { return ResponseEntity.ok(service.update(id, entity)); }\r\n    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
}