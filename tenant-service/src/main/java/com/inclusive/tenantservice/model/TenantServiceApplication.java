package com.inclusive.tenantservice.model;\r\nimport javax.persistence.*;\r\n@Entity
public class TenantServiceApplication {\r\n    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;\r\n    // Campos detectados (manual):
    // id\r\n    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}