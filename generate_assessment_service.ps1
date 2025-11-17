# ======================================================================
# ILP - FULL RECONSTRUCTION OF assessment-service (UTF-8 WITHOUT BOM)
# Option C – Replace completely the module with a clean, functional module
# ======================================================================

Write-Host "==============================================="
Write-Host "   ILP - Rebuild MODULE: assessment-service"
Write-Host "==============================================="

$ModuleName = "assessment-service"
$BasePath = Join-Path (Get-Location) $ModuleName

# -----------------------------
# 1. DELETE EXISTING DIRECTORY
# -----------------------------
if (Test-Path $BasePath) {
    Write-Host "Removing existing module: $BasePath"
    Remove-Item -Recurse -Force $BasePath
}

# -----------------------------
# 2. CREATE NEW DIRECTORY TREE
# -----------------------------
Write-Host "Creating directory structure..."

New-Item -ItemType Directory -Path "$BasePath"
New-Item -ItemType Directory -Path "$BasePath/src/main/java/com/inclusive/assessmentservice" -Force
New-Item -ItemType Directory -Path "$BasePath/src/main/resources" -Force

$EntityPath   = "$BasePath/src/main/java/com/inclusive/assessmentservice/entity"
$DTOPath      = "$BasePath/src/main/java/com/inclusive/assessmentservice/dto"
$RepoPath     = "$BasePath/src/main/java/com/inclusive/assessmentservice/repository"
$ServicePath  = "$BasePath/src/main/java/com/inclusive/assessmentservice/service"
$ImplPath     = "$BasePath/src/main/java/com/inclusive/assessmentservice/service/impl"
$ControllerPath = "$BasePath/src/main/java/com/inclusive/assessmentservice/controller"

New-Item -ItemType Directory -Path $EntityPath -Force
New-Item -ItemType Directory -Path $DTOPath -Force
New-Item -ItemType Directory -Path $RepoPath -Force
New-Item -ItemType Directory -Path $ServicePath -Force
New-Item -ItemType Directory -Path $ImplPath -Force
New-Item -ItemType Directory -Path $ControllerPath -Force

# -----------------------------
# 3. CONTENT TEMPLATES
# -----------------------------

# ==== POM.XML ====
$PomContent = @"
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">

  <modelVersion>4.0.0</modelVersion>

  <parent>
    <groupId>com.inclusive.platform</groupId>
    <artifactId>inclusive-learning-platform-backend</artifactId>
    <version>1.0.0-SNAPSHOT</version>
  </parent>

  <artifactId>assessment-service</artifactId>
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
      <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>

    <dependency>
      <groupId>com.inclusive.platform</groupId>
      <artifactId>commons-service</artifactId>
      <version>1.0.0-SNAPSHOT</version>
    </dependency>

    <dependency>
      <groupId>com.h2database</groupId>
      <artifactId>h2</artifactId>
      <scope>runtime</scope>
    </dependency>
  </dependencies>

</project>
"@

Set-Content -Path "$BasePath/pom.xml" -Value $PomContent -Encoding UTF8

# ==== application.yml ====
$AppConfig = @"
server:
  port: 8094

spring:
  datasource:
    url: jdbc:h2:mem:assessmentdb
    driverClassName: org.h2.Driver
    username: sa
    password: ""
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
"@

Set-Content -Path "$BasePath/src/main/resources/application.yml" -Value $AppConfig -Encoding UTF8

# ==== MAIN APPLICATION ====
$MainApp = @"
package com.inclusive.assessmentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AssessmentServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AssessmentServiceApplication.class, args);
    }
}
"@

Set-Content -Path "$BasePath/src/main/java/com/inclusive/assessmentservice/AssessmentServiceApplication.java" -Value $MainApp -Encoding UTF8

# -----------------------------
# 4. ENTITIES
# -----------------------------
$Entities = @{
    "Assessment" = @"
package com.inclusive.assessmentservice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Assessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String type;
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getType() { return type; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setUserId(Long userId) { this.userId = userId; }
    public void setType(String type) { this.type = type; }
}
"@
}

foreach ($name in $Entities.Keys) {
    Set-Content -Path "$EntityPath/$name.java" -Value $Entities[$name] -Encoding UTF8
}

# -----------------------------
# 5. DTOs
# -----------------------------
$DTOs = @{
    "AssessmentDTO" = @"
package com.inclusive.assessmentservice.dto;

public class AssessmentDTO {

    private Long id;
    private Long userId;
    private String type;

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getType() { return type; }

    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setType(String type) { this.type = type; }
}
"@
}

foreach ($name in $DTOs.Keys) {
    Set-Content -Path "$DTOPath/$name.java" -Value $DTOs[$name] -Encoding UTF8
}

# -----------------------------
# 6. REPOSITORY
# -----------------------------
$RepoContent = @"
package com.inclusive.assessmentservice.repository;

import com.inclusive.assessmentservice.entity.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssessmentRepository extends JpaRepository<Assessment, Long> {
}
"@

Set-Content -Path "$RepoPath/AssessmentRepository.java" -Value $RepoContent -Encoding UTF8

# -----------------------------
# 7. SERVICE + IMPL
# -----------------------------
$ServiceContent = @"
package com.inclusive.assessmentservice.service;

import com.inclusive.assessmentservice.dto.AssessmentDTO;
import java.util.List;

public interface AssessmentService {
    List<AssessmentDTO> findAll();
    AssessmentDTO findById(Long id);
    AssessmentDTO create(AssessmentDTO dto);
    AssessmentDTO update(Long id, AssessmentDTO dto);
    void delete(Long id);
}
"@

Set-Content -Path "$ServicePath/AssessmentService.java" -Value $ServiceContent -Encoding UTF8

$ImplContent = @"
package com.inclusive.assessmentservice.service.impl;

import com.inclusive.assessmentservice.dto.AssessmentDTO;
import com.inclusive.assessmentservice.entity.Assessment;
import com.inclusive.assessmentservice.repository.AssessmentRepository;
import com.inclusive.assessmentservice.service.AssessmentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssessmentServiceImpl implements AssessmentService {

    private final AssessmentRepository repo;

    public AssessmentServiceImpl(AssessmentRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<AssessmentDTO> findAll() {
        return repo.findAll().stream().map(this::map).collect(Collectors.toList());
    }

    @Override
    public AssessmentDTO findById(Long id) {
        return repo.findById(id).map(this::map).orElse(null);
    }

    @Override
    public AssessmentDTO create(AssessmentDTO dto) {
        Assessment a = new Assessment();
        a.setUserId(dto.getUserId());
        a.setType(dto.getType());
        return map(repo.save(a));
    }

    @Override
    public AssessmentDTO update(Long id, AssessmentDTO dto) {
        Assessment a = repo.findById(id).orElseThrow();
        a.setUserId(dto.getUserId());
        a.setType(dto.getType());
        return map(repo.save(a));
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }

    private AssessmentDTO map(Assessment a) {
        AssessmentDTO dto = new AssessmentDTO();
        dto.setId(a.getId());
        dto.setUserId(a.getUserId());
        dto.setType(a.getType());
        return dto;
    }
}
"@

Set-Content -Path "$ImplPath/AssessmentServiceImpl.java" -Value $ImplContent -Encoding UTF8

# -----------------------------
# 8. CONTROLLER
# -----------------------------
$Controller = @"
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
"@

Set-Content -Path "$ControllerPath/AssessmentController.java" -Value $Controller -Encoding UTF8

# -----------------------------
# 9. FIN
# -----------------------------
Write-Host "==============================================="
Write-Host " assessment-service RECONSTRUCTION COMPLETED"
Write-Host " Now run:"
Write-Host "   mvn clean package -pl assessment-service -am"
Write-Host "==============================================="
