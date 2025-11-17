param(
    [string]$ProjectRoot = (Get-Location).Path
)

$ErrorActionPreference = "Stop"

function Write-FileNoBom {
    param(
        [string]$Path,
        [string]$Content
    )
    $dir = Split-Path $Path -Parent
    if (-not (Test-Path $dir)) {
        New-Item -ItemType Directory -Path $dir -Force | Out-Null
    }
    $utf8NoBom = New-Object System.Text.UTF8Encoding $false
    [System.IO.File]::WriteAllText($Path, $Content, $utf8NoBom)
    Write-Host "[OK] Wrote: $Path"
}

Write-Host "==============================================="
Write-Host " ILP - Rebuild FAILED MODULES (NO BOM, ALL) "
Write-Host "==============================================="
Write-Host "Project root: $ProjectRoot"
Write-Host ""

# -------------------------------------------------
# TENANT-SERVICE
# -------------------------------------------------
$tenantModule = "tenant-service"
$tenantPath = Join-Path $ProjectRoot $tenantModule

Write-Host "-----------------------------------------------"
Write-Host " REBUILDING MODULE: tenant-service"
Write-Host "-----------------------------------------------"

if (Test-Path $tenantPath) {
    Write-Host "Removing existing tenant-service directory..."
    Remove-Item -Recurse -Force $tenantPath
}

New-Item -ItemType Directory -Path $tenantPath | Out-Null

# pom.xml for tenant-service
$tenantPom = @'
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.inclusive.platform</groupId>
        <artifactId>inclusive-learning-platform-backend</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>tenant-service</artifactId>
    <packaging>jar</packaging>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>com.inclusive.platform</groupId>
            <artifactId>commons-service</artifactId>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>jakarta.validation</groupId>
            <artifactId>jakarta.validation-api</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

</project>
'@
Write-FileNoBom -Path (Join-Path $tenantPath "pom.xml") -Content $tenantPom

$tenantBase = Join-Path $tenantPath "src\main\java\com\inclusive\tenantservice"
New-Item -ItemType Directory -Path (Join-Path $tenantBase "entity") -Force | Out-Null
New-Item -ItemType Directory -Path (Join-Path $tenantBase "dto") -Force | Out-Null
New-Item -ItemType Directory -Path (Join-Path $tenantBase "mapper") -Force | Out-Null
New-Item -ItemType Directory -Path (Join-Path $tenantBase "repository") -Force | Out-Null
New-Item -ItemType Directory -Path (Join-Path $tenantBase "service") -Force | Out-Null
New-Item -ItemType Directory -Path (Join-Path $tenantBase "service\impl") -Force | Out-Null
New-Item -ItemType Directory -Path (Join-Path $tenantBase "controller") -Force | Out-Null
New-Item -ItemType Directory -Path (Join-Path $tenantPath "src\main\resources") -Force | Out-Null

# TenantServiceApplication
$tenantApp = @'
package com.inclusive.tenantservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TenantServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TenantServiceApplication.class, args);
    }
}
'@
Write-FileNoBom -Path (Join-Path $tenantBase "TenantServiceApplication.java") -Content $tenantApp

# Tenant entity
$tenantEntity = @'
package com.inclusive.tenantservice.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "tenants")
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 100)
    private String code;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", length = 512)
    private String description;

    @Column(name = "active", nullable = false)
    private Boolean active = Boolean.TRUE;

    @Column(name = "contact_email", length = 150)
    private String contactEmail;

    @Column(name = "contact_phone", length = 40)
    private String contactPhone;

    @Column(name = "country", length = 80)
    private String country;

    @Column(name = "timezone", length = 80)
    private String timezone;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (active == null) {
            active = Boolean.TRUE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public Tenant() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
'@
Write-FileNoBom -Path (Join-Path $tenantBase "entity\Tenant.java") -Content $tenantEntity

# TenantDTO
$tenantDto = @'
package com.inclusive.tenantservice.dto;

import java.time.Instant;

public class TenantDTO {

    private Long id;
    private String code;
    private String name;
    private String description;
    private Boolean active;
    private String contactEmail;
    private String contactPhone;
    private String country;
    private String timezone;
    private Instant createdAt;
    private Instant updatedAt;

    public TenantDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
'@
Write-FileNoBom -Path (Join-Path $tenantBase "dto\TenantDTO.java") -Content $tenantDto

# TenantMapper
$tenantMapper = @'
package com.inclusive.tenantservice.mapper;

import com.inclusive.tenantservice.dto.TenantDTO;
import com.inclusive.tenantservice.entity.Tenant;

public final class TenantMapper {

    private TenantMapper() {
    }

    public static TenantDTO toDto(Tenant entity) {
        if (entity == null) {
            return null;
        }
        TenantDTO dto = new TenantDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setActive(entity.getActive());
        dto.setContactEmail(entity.getContactEmail());
        dto.setContactPhone(entity.getContactPhone());
        dto.setCountry(entity.getCountry());
        dto.setTimezone(entity.getTimezone());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    public static void updateEntity(Tenant entity, TenantDTO dto) {
        entity.setCode(dto.getCode());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setActive(dto.getActive());
        entity.setContactEmail(dto.getContactEmail());
        entity.setContactPhone(dto.getContactPhone());
        entity.setCountry(dto.getCountry());
        entity.setTimezone(dto.getTimezone());
    }

    public static Tenant toNewEntity(TenantDTO dto) {
        Tenant entity = new Tenant();
        updateEntity(entity, dto);
        return entity;
    }
}
'@
Write-FileNoBom -Path (Join-Path $tenantBase "mapper\TenantMapper.java") -Content $tenantMapper

# TenantRepository
$tenantRepo = @'
package com.inclusive.tenantservice.repository;

import com.inclusive.tenantservice.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TenantRepository extends JpaRepository<Tenant, Long> {

    Optional<Tenant> findByCode(String code);

    boolean existsByCode(String code);
}
'@
Write-FileNoBom -Path (Join-Path $tenantBase "repository\TenantRepository.java") -Content $tenantRepo

# TenantService interface
$tenantService = @'
package com.inclusive.tenantservice.service;

import com.inclusive.tenantservice.dto.TenantDTO;

import java.util.List;

public interface TenantService {

    List<TenantDTO> findAll();

    TenantDTO findById(Long id);

    TenantDTO create(TenantDTO dto);

    TenantDTO update(Long id, TenantDTO dto);

    void delete(Long id);
}
'@
Write-FileNoBom -Path (Join-Path $tenantBase "service\TenantService.java") -Content $tenantService

# TenantServiceImpl
$tenantServiceImpl = @'
package com.inclusive.tenantservice.service.impl;

import com.inclusive.tenantservice.dto.TenantDTO;
import com.inclusive.tenantservice.entity.Tenant;
import com.inclusive.tenantservice.mapper.TenantMapper;
import com.inclusive.tenantservice.repository.TenantRepository;
import com.inclusive.tenantservice.service.TenantService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;

    public TenantServiceImpl(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TenantDTO> findAll() {
        return tenantRepository.findAll()
                .stream()
                .map(TenantMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TenantDTO findById(Long id) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tenant not found"));
        return TenantMapper.toDto(tenant);
    }

    @Override
    public TenantDTO create(TenantDTO dto) {
        if (dto.getCode() != null && tenantRepository.existsByCode(dto.getCode())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tenant code already exists");
        }
        Tenant tenant = TenantMapper.toNewEntity(dto);
        Tenant saved = tenantRepository.save(tenant);
        return TenantMapper.toDto(saved);
    }

    @Override
    public TenantDTO update(Long id, TenantDTO dto) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tenant not found"));
        if (dto.getCode() != null && !dto.getCode().equals(tenant.getCode())
                && tenantRepository.existsByCode(dto.getCode())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tenant code already exists");
        }
        TenantMapper.updateEntity(tenant, dto);
        Tenant saved = tenantRepository.save(tenant);
        return TenantMapper.toDto(saved);
    }

    @Override
    public void delete(Long id) {
        if (!tenantRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tenant not found");
        }
        tenantRepository.deleteById(id);
    }
}
'@
Write-FileNoBom -Path (Join-Path $tenantBase "service\impl\TenantServiceImpl.java") -Content $tenantServiceImpl

# TenantController
$tenantController = @'
package com.inclusive.tenantservice.controller;

import com.inclusive.tenantservice.dto.TenantDTO;
import com.inclusive.tenantservice.service.TenantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tenants")
public class TenantController {

    private final TenantService tenantService;

    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @GetMapping
    public ResponseEntity<List<TenantDTO>> getAllTenants() {
        return ResponseEntity.ok(tenantService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TenantDTO> getTenantById(@PathVariable Long id) {
        return ResponseEntity.ok(tenantService.findById(id));
    }

    @PostMapping
    public ResponseEntity<TenantDTO> createTenant(@RequestBody TenantDTO dto) {
        TenantDTO created = tenantService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TenantDTO> updateTenant(@PathVariable Long id, @RequestBody TenantDTO dto) {
        TenantDTO updated = tenantService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTenant(@PathVariable Long id) {
        tenantService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
'@
Write-FileNoBom -Path (Join-Path $tenantBase "controller\TenantController.java") -Content $tenantController

# application.yml
$tenantYml = @'
spring:
  application:
    name: tenant-service

server:
  port: 0

management:
  endpoints:
    web:
      exposure:
        include: health,info
'@
Write-FileNoBom -Path (Join-Path $tenantPath "src\main\resources\application.yml") -Content $tenantYml

Write-Host "tenant-service rebuild completed."
Write-Host ""

# -------------------------------------------------
# MONITORING-SERVICE
# -------------------------------------------------
$monitorModule = "monitoring-service"
$monitorPath = Join-Path $ProjectRoot $monitorModule

Write-Host "-----------------------------------------------"
Write-Host " REBUILDING MODULE: monitoring-service"
Write-Host "-----------------------------------------------"

if (Test-Path $monitorPath) {
    Write-Host "Removing existing monitoring-service directory..."
    Remove-Item -Recurse -Force $monitorPath
}

New-Item -ItemType Directory -Path $monitorPath | Out-Null

# pom.xml for monitoring-service
$monitorPom = @'
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.inclusive.platform</groupId>
        <artifactId>inclusive-learning-platform-backend</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>monitoring-service</artifactId>
    <packaging>jar</packaging>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>com.inclusive.platform</groupId>
            <artifactId>commons-service</artifactId>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>jakarta.validation</groupId>
            <artifactId>jakarta.validation-api</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

</project>
'@
Write-FileNoBom -Path (Join-Path $monitorPath "pom.xml") -Content $monitorPom

$monitorBase = Join-Path $monitorPath "src\main\java\com\inclusive\monitoringservice"
New-Item -ItemType Directory -Path (Join-Path $monitorBase "entity") -Force | Out-Null
New-Item -ItemType Directory -Path (Join-Path $monitorBase "dto") -Force | Out-Null
New-Item -ItemType Directory -Path (Join-Path $monitorBase "mapper") -Force | Out-Null
New-Item -ItemType Directory -Path (Join-Path $monitorBase "repository") -Force | Out-Null
New-Item -ItemType Directory -Path (Join-Path $monitorBase "service") -Force | Out-Null
New-Item -ItemType Directory -Path (Join-Path $monitorBase "service\impl") -Force | Out-Null
New-Item -ItemType Directory -Path (Join-Path $monitorBase "controller") -Force | Out-Null
New-Item -ItemType Directory -Path (Join-Path $monitorPath "src\main\resources") -Force | Out-Null

# MonitoringServiceApplication
$monitorApp = @'
package com.inclusive.monitoringservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MonitoringServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonitoringServiceApplication.class, args);
    }
}
'@
Write-FileNoBom -Path (Join-Path $monitorBase "MonitoringServiceApplication.java") -Content $monitorApp

# MonitoringEvent entity
$monitorEntity = @'
package com.inclusive.monitoringservice.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "monitoring_events")
public class MonitoringEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "service_name", nullable = false, length = 150)
    private String serviceName;

    @Column(name = "tenant_code", length = 100)
    private String tenantCode;

    @Column(name = "event_type", length = 80)
    private String eventType;

    @Column(name = "severity", length = 40)
    private String severity;

    @Column(name = "message", length = 1024)
    private String message;

    @Column(name = "payload", length = 4000)
    private String payload;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    public MonitoringEvent() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
'@
Write-FileNoBom -Path (Join-Path $monitorBase "entity\MonitoringEvent.java") -Content $monitorEntity

# MonitoringEventDTO
$monitorDto = @'
package com.inclusive.monitoringservice.dto;

import java.time.Instant;

public class MonitoringEventDTO {

    private Long id;
    private String serviceName;
    private String tenantCode;
    private String eventType;
    private String severity;
    private String message;
    private String payload;
    private Instant createdAt;

    public MonitoringEventDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
'@
Write-FileNoBom -Path (Join-Path $monitorBase "dto\MonitoringEventDTO.java") -Content $monitorDto

# MonitoringEventMapper
$monitorMapper = @'
package com.inclusive.monitoringservice.mapper;

import com.inclusive.monitoringservice.dto.MonitoringEventDTO;
import com.inclusive.monitoringservice.entity.MonitoringEvent;

public final class MonitoringEventMapper {

    private MonitoringEventMapper() {
    }

    public static MonitoringEventDTO toDto(MonitoringEvent entity) {
        if (entity == null) {
            return null;
        }
        MonitoringEventDTO dto = new MonitoringEventDTO();
        dto.setId(entity.getId());
        dto.setServiceName(entity.getServiceName());
        dto.setTenantCode(entity.getTenantCode());
        dto.setEventType(entity.getEventType());
        dto.setSeverity(entity.getSeverity());
        dto.setMessage(entity.getMessage());
        dto.setPayload(entity.getPayload());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }

    public static MonitoringEvent toNewEntity(MonitoringEventDTO dto) {
        MonitoringEvent entity = new MonitoringEvent();
        entity.setServiceName(dto.getServiceName());
        entity.setTenantCode(dto.getTenantCode());
        entity.setEventType(dto.getEventType());
        entity.setSeverity(dto.getSeverity());
        entity.setMessage(dto.getMessage());
        entity.setPayload(dto.getPayload());
        return entity;
    }
}
'@
Write-FileNoBom -Path (Join-Path $monitorBase "mapper\MonitoringEventMapper.java") -Content $monitorMapper

# MonitoringEventRepository
$monitorRepo = @'
package com.inclusive.monitoringservice.repository;

import com.inclusive.monitoringservice.entity.MonitoringEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MonitoringEventRepository extends JpaRepository<MonitoringEvent, Long> {

    List<MonitoringEvent> findByServiceName(String serviceName);

    List<MonitoringEvent> findBySeverity(String severity);
}
'@
Write-FileNoBom -Path (Join-Path $monitorBase "repository\MonitoringEventRepository.java") -Content $monitorRepo

# MonitoringEventService
$monitorService = @'
package com.inclusive.monitoringservice.service;

import com.inclusive.monitoringservice.dto.MonitoringEventDTO;

import java.util.List;

public interface MonitoringEventService {

    List<MonitoringEventDTO> findAll();

    MonitoringEventDTO findById(Long id);

    MonitoringEventDTO create(MonitoringEventDTO dto);

    void delete(Long id);
}
'@
Write-FileNoBom -Path (Join-Path $monitorBase "service\MonitoringEventService.java") -Content $monitorService

# MonitoringEventServiceImpl
$monitorServiceImpl = @'
package com.inclusive.monitoringservice.service.impl;

import com.inclusive.monitoringservice.dto.MonitoringEventDTO;
import com.inclusive.monitoringservice.entity.MonitoringEvent;
import com.inclusive.monitoringservice.mapper.MonitoringEventMapper;
import com.inclusive.monitoringservice.repository.MonitoringEventRepository;
import com.inclusive.monitoringservice.service.MonitoringEventService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MonitoringEventServiceImpl implements MonitoringEventService {

    private final MonitoringEventRepository repository;

    public MonitoringEventServiceImpl(MonitoringEventRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MonitoringEventDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(MonitoringEventMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MonitoringEventDTO findById(Long id) {
        MonitoringEvent event = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Monitoring event not found"));
        return MonitoringEventMapper.toDto(event);
    }

    @Override
    public MonitoringEventDTO create(MonitoringEventDTO dto) {
        MonitoringEvent entity = MonitoringEventMapper.toNewEntity(dto);
        MonitoringEvent saved = repository.save(entity);
        return MonitoringEventMapper.toDto(saved);
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Monitoring event not found");
        }
        repository.deleteById(id);
    }
}
'@
Write-FileNoBom -Path (Join-Path $monitorBase "service\impl\MonitoringEventServiceImpl.java") -Content $monitorServiceImpl

# MonitoringEventController
$monitorController = @'
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
'@
Write-FileNoBom -Path (Join-Path $monitorBase "controller\MonitoringEventController.java") -Content $monitorController

# application.yml
$monitorYml = @'
spring:
  application:
    name: monitoring-service

server:
  port: 0

management:
  endpoints:
    web:
      exposure:
        include: health,info
'@
Write-FileNoBom -Path (Join-Path $monitorPath "src\main\resources\application.yml") -Content $monitorYml

Write-Host "monitoring-service rebuild completed."
Write-Host ""

# -------------------------------------------------
# REPORT-SERVICE
# -------------------------------------------------
$reportModule = "report-service"
$reportPath = Join-Path $ProjectRoot $reportModule

Write-Host "-----------------------------------------------"
Write-Host " REBUILDING MODULE: report-service"
Write-Host "-----------------------------------------------"

if (Test-Path $reportPath) {
    Write-Host "Removing existing report-service directory..."
    Remove-Item -Recurse -Force $reportPath
}

New-Item -ItemType Directory -Path $reportPath | Out-Null

# pom.xml for report-service
$reportPom = @'
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.inclusive.platform</groupId>
        <artifactId>inclusive-learning-platform-backend</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>report-service</artifactId>
    <packaging>jar</packaging>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>com.inclusive.platform</groupId>
            <artifactId>commons-service</artifactId>
        </dependency>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>jakarta.validation</groupId>
            <artifactId>jakarta.validation-api</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

</project>
'@
Write-FileNoBom -Path (Join-Path $reportPath "pom.xml") -Content $reportPom

$reportBase = Join-Path $reportPath "src\main\java\com\inclusive\reportservice"
New-Item -ItemType Directory -Path (Join-Path $reportBase "entity") -Force | Out-Null
New-Item -ItemType Directory -Path (Join-Path $reportBase "dto") -Force | Out-Null
New-Item -ItemType Directory -Path (Join-Path $reportBase "mapper") -Force | Out-Null
New-Item -ItemType Directory -Path (Join-Path $reportBase "repository") -Force | Out-Null
New-Item -ItemType Directory -Path (Join-Path $reportBase "service") -Force | Out-Null
New-Item -ItemType Directory -Path (Join-Path $reportBase "service\impl") -Force | Out-Null
New-Item -ItemType Directory -Path (Join-Path $reportBase "controller") -Force | Out-Null
New-Item -ItemType Directory -Path (Join-Path $reportPath "src\main\resources") -Force | Out-Null

# ReportServiceApplication
$reportApp = @'
package com.inclusive.reportservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ReportServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReportServiceApplication.class, args);
    }
}
'@
Write-FileNoBom -Path (Join-Path $reportBase "ReportServiceApplication.java") -Content $reportApp

# ReportJob entity
$reportEntity = @'
package com.inclusive.reportservice.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "report_jobs")
public class ReportJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_code", length = 100)
    private String tenantCode;

    @Column(name = "report_type", nullable = false, length = 150)
    private String reportType;

    @Column(name = "status", nullable = false, length = 40)
    private String status;

    @Column(name = "requested_by", length = 150)
    private String requestedBy;

    @Column(name = "requested_at", nullable = false, updatable = false)
    private Instant requestedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "output_url", length = 1024)
    private String outputUrl;

    @Column(name = "error_message", length = 2000)
    private String errorMessage;

    @PrePersist
    protected void onCreate() {
        this.requestedAt = Instant.now();
        if (this.status == null) {
            this.status = "PENDING";
        }
    }

    public ReportJob() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public Instant getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(Instant requestedAt) {
        this.requestedAt = requestedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    public String getOutputUrl() {
        return outputUrl;
    }

    public void setOutputUrl(String outputUrl) {
        this.outputUrl = outputUrl;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
'@
Write-FileNoBom -Path (Join-Path $reportBase "entity\ReportJob.java") -Content $reportEntity

# ReportJobDTO
$reportDto = @'
package com.inclusive.reportservice.dto;

import java.time.Instant;

public class ReportJobDTO {

    private Long id;
    private String tenantCode;
    private String reportType;
    private String status;
    private String requestedBy;
    private Instant requestedAt;
    private Instant completedAt;
    private String outputUrl;
    private String errorMessage;

    public ReportJobDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public Instant getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(Instant requestedAt) {
        this.requestedAt = requestedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    public String getOutputUrl() {
        return outputUrl;
    }

    public void setOutputUrl(String outputUrl) {
        this.outputUrl = outputUrl;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
'@
Write-FileNoBom -Path (Join-Path $reportBase "dto\ReportJobDTO.java") -Content $reportDto

# ReportJobMapper
$reportMapper = @'
package com.inclusive.reportservice.mapper;

import com.inclusive.reportservice.dto.ReportJobDTO;
import com.inclusive.reportservice.entity.ReportJob;

public final class ReportJobMapper {

    private ReportJobMapper() {
    }

    public static ReportJobDTO toDto(ReportJob entity) {
        if (entity == null) {
            return null;
        }
        ReportJobDTO dto = new ReportJobDTO();
        dto.setId(entity.getId());
        dto.setTenantCode(entity.getTenantCode());
        dto.setReportType(entity.getReportType());
        dto.setStatus(entity.getStatus());
        dto.setRequestedBy(entity.getRequestedBy());
        dto.setRequestedAt(entity.getRequestedAt());
        dto.setCompletedAt(entity.getCompletedAt());
        dto.setOutputUrl(entity.getOutputUrl());
        dto.setErrorMessage(entity.getErrorMessage());
        return dto;
    }

    public static void updateEntity(ReportJob entity, ReportJobDTO dto) {
        entity.setTenantCode(dto.getTenantCode());
        entity.setReportType(dto.getReportType());
        entity.setStatus(dto.getStatus());
        entity.setRequestedBy(dto.getRequestedBy());
        entity.setRequestedAt(dto.getRequestedAt());
        entity.setCompletedAt(dto.getCompletedAt());
        entity.setOutputUrl(dto.getOutputUrl());
        entity.setErrorMessage(dto.getErrorMessage());
    }

    public static ReportJob toNewEntity(ReportJobDTO dto) {
        ReportJob entity = new ReportJob();
        updateEntity(entity, dto);
        return entity;
    }
}
'@
Write-FileNoBom -Path (Join-Path $reportBase "mapper\ReportJobMapper.java") -Content $reportMapper

# ReportJobRepository
$reportRepo = @'
package com.inclusive.reportservice.repository;

import com.inclusive.reportservice.entity.ReportJob;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportJobRepository extends JpaRepository<ReportJob, Long> {
}
'@
Write-FileNoBom -Path (Join-Path $reportBase "repository\ReportJobRepository.java") -Content $reportRepo

# ReportJobService
$reportService = @'
package com.inclusive.reportservice.service;

import com.inclusive.reportservice.dto.ReportJobDTO;

import java.util.List;

public interface ReportJobService {

    List<ReportJobDTO> findAll();

    ReportJobDTO findById(Long id);

    ReportJobDTO create(ReportJobDTO dto);

    ReportJobDTO update(Long id, ReportJobDTO dto);

    void delete(Long id);
}
'@
Write-FileNoBom -Path (Join-Path $reportBase "service\ReportJobService.java") -Content $reportService

# ReportJobServiceImpl
$reportServiceImpl = @'
package com.inclusive.reportservice.service.impl;

import com.inclusive.reportservice.dto.ReportJobDTO;
import com.inclusive.reportservice.entity.ReportJob;
import com.inclusive.reportservice.mapper.ReportJobMapper;
import com.inclusive.reportservice.repository.ReportJobRepository;
import com.inclusive.reportservice.service.ReportJobService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReportJobServiceImpl implements ReportJobService {

    private final ReportJobRepository repository;

    public ReportJobServiceImpl(ReportJobRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReportJobDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(ReportJobMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ReportJobDTO findById(Long id) {
        ReportJob job = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Report job not found"));
        return ReportJobMapper.toDto(job);
    }

    @Override
    public ReportJobDTO create(ReportJobDTO dto) {
        ReportJob job = ReportJobMapper.toNewEntity(dto);
        ReportJob saved = repository.save(job);
        return ReportJobMapper.toDto(saved);
    }

    @Override
    public ReportJobDTO update(Long id, ReportJobDTO dto) {
        ReportJob job = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Report job not found"));
        ReportJobMapper.updateEntity(job, dto);
        ReportJob saved = repository.save(job);
        return ReportJobMapper.toDto(saved);
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Report job not found");
        }
        repository.deleteById(id);
    }
}
'@
Write-FileNoBom -Path (Join-Path $reportBase "service\impl\ReportJobServiceImpl.java") -Content $reportServiceImpl

# ReportJobController
$reportController = @'
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
'@
Write-FileNoBom -Path (Join-Path $reportBase "controller\ReportJobController.java") -Content $reportController

# application.yml
$reportYml = @'
spring:
  application:
    name: report-service

server:
  port: 0

management:
  endpoints:
    web:
      exposure:
        include: health,info
'@
Write-FileNoBom -Path (Join-Path $reportPath "src\main\resources\application.yml") -Content $reportYml

Write-Host "report-service rebuild completed."
Write-Host ""

# -------------------------------------------------
# GATEWAY-SERVICE
# -------------------------------------------------
$gatewayModule = "gateway-service"
$gatewayPath = Join-Path $ProjectRoot $gatewayModule

Write-Host "-----------------------------------------------"
Write-Host " REBUILDING MODULE: gateway-service"
Write-Host "-----------------------------------------------"

if (Test-Path $gatewayPath) {
    Write-Host "Removing existing gateway-service directory..."
    Remove-Item -Recurse -Force $gatewayPath
}

New-Item -ItemType Directory -Path $gatewayPath | Out-Null

# pom.xml for gateway-service
$gatewayPom = @'
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.inclusive.platform</groupId>
        <artifactId>inclusive-learning-platform-backend</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>

    <artifactId>gateway-service</artifactId>
    <packaging>jar</packaging>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-webflux</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>com.inclusive.platform</groupId>
            <artifactId>commons-service</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

</project>
'@
Write-FileNoBom -Path (Join-Path $gatewayPath "pom.xml") -Content $gatewayPom

$gatewayBase = Join-Path $gatewayPath "src\main\java\com\inclusive\gatewayservice"
New-Item -ItemType Directory -Path $gatewayBase -Force | Out-Null
New-Item -ItemType Directory -Path (Join-Path $gatewayBase "controller") -Force | Out-Null
New-Item -ItemType Directory -Path (Join-Path $gatewayPath "src\main\resources") -Force | Out-Null

# GatewayServiceApplication
$gatewayApp = @'
package com.inclusive.gatewayservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GatewayServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApplication.class, args);
    }
}
'@
Write-FileNoBom -Path (Join-Path $gatewayBase "GatewayServiceApplication.java") -Content $gatewayApp

# Simple info controller
$gatewayController = @'
package com.inclusive.gatewayservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class GatewayInfoController {

    @GetMapping("/api/gateway/info")
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> data = new HashMap<>();
        data.put("service", "gateway-service");
        data.put("status", "UP");
        data.put("description", "API gateway for Inclusive Learning Platform");
        return ResponseEntity.ok(data);
    }
}
'@
Write-FileNoBom -Path (Join-Path $gatewayBase "controller\GatewayInfoController.java") -Content $gatewayController

# application.yml
$gatewayYml = @'
spring:
  application:
    name: gateway-service

server:
  port: 8080

management:
  endpoints:
    web:
      exposure:
        include: health,info

# You can extend routes configuration here when needed.
'@
Write-FileNoBom -Path (Join-Path $gatewayPath "src\main\resources\application.yml") -Content $gatewayYml

Write-Host "gateway-service rebuild completed."
Write-Host ""

Write-Host "==============================================="
Write-Host " ALL FAILED MODULES REBUILT WITHOUT BOM"
Write-Host " Modules:"
Write-Host "  - tenant-service"
Write-Host "  - monitoring-service"
Write-Host "  - report-service"
Write-Host "  - gateway-service"
Write-Host "Now run:"
Write-Host "  mvn clean package -pl tenant-service,monitoring-service,report-service,gateway-service -am"
Write-Host "or:"
Write-Host "  mvn clean package -am"
Write-Host "==============================================="
