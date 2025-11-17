# ============================================
# rebuild_ilp_backend_core.ps1
# Reconstruye el nucleo comun + modulo adaptive-education-service
# para un MVP accesible y apto para IA.
# Ejecutar desde la raiz del repo:
#   PS> cd C:\temp_ilp\inclusive-learning-platform-backend
#   PS> .\rebuild_ilp_backend_core.ps1
# ============================================

$ErrorActionPreference = "Stop"

# Usamos la carpeta actual como raiz del proyecto
$root = Get-Location

function Write-JavaFile {
    param(
        [string]$Path,
        [string]$Content
    )
    $dir = Split-Path $Path
    if (-not (Test-Path $dir)) {
        New-Item -ItemType Directory -Path $dir -Force | Out-Null
    }
    # ASCII para evitar BOM y caracteres raros
    Set-Content -Path $Path -Value $Content -Encoding Ascii
    Write-Host "Wrote $Path" -ForegroundColor Green
}

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host " REBUILD ILP BACKEND CORE (COMMONS+ADAPTIVE)" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan

# ===========================================
# 1. commons-service: StudentDTO centralizado
# ===========================================

$commonsJavaRoot = Join-Path $root "commons-service\src\main\java\com\inclusive\common\dto"
if (-not (Test-Path $commonsJavaRoot)) {
    New-Item -ItemType Directory -Path $commonsJavaRoot -Force | Out-Null
}

$studentDtoPath = Join-Path $commonsJavaRoot "StudentDTO.java"

$studentDtoContent = @"
package com.inclusive.common.dto;

/**
 * StudentDTO
 *
 * Accessible and ML friendly representation of a student.
 * This DTO is shared across services to build consistent datasets.
 */
public class StudentDTO {

    private Long id;

    // Identity
    private String firstName;
    private String lastName;
    private String email;

    // Basic academic info
    private Integer age;
    private String grade;

    // Learning and accessibility features
    private String preferredModality; // visual, auditory, kinesthetic, multimodal
    private Boolean screenReaderUser;
    private Boolean signLanguageUser;
    private Boolean highContrastRequired;
    private Boolean captionsRequired;
    private Boolean keyboardNavigationPreferred;
    private String assistiveTechnology;  // switch device, eye tracker, etc.
    private String accessibilityNotes;   // free text for educators and AI

    public StudentDTO() {
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getPreferredModality() {
        return preferredModality;
    }

    public void setPreferredModality(String preferredModality) {
        this.preferredModality = preferredModality;
    }

    public Boolean getScreenReaderUser() {
        return screenReaderUser;
    }

    public void setScreenReaderUser(Boolean screenReaderUser) {
        this.screenReaderUser = screenReaderUser;
    }

    public Boolean getSignLanguageUser() {
        return signLanguageUser;
    }

    public void setSignLanguageUser(Boolean signLanguageUser) {
        this.signLanguageUser = signLanguageUser;
    }

    public Boolean getHighContrastRequired() {
        return highContrastRequired;
    }

    public void setHighContrastRequired(Boolean highContrastRequired) {
        this.highContrastRequired = highContrastRequired;
    }

    public Boolean getCaptionsRequired() {
        return captionsRequired;
    }

    public void setCaptionsRequired(Boolean captionsRequired) {
        this.captionsRequired = captionsRequired;
    }

    public Boolean getKeyboardNavigationPreferred() {
        return keyboardNavigationPreferred;
    }

    public void setKeyboardNavigationPreferred(Boolean keyboardNavigationPreferred) {
        this.keyboardNavigationPreferred = keyboardNavigationPreferred;
    }

    public String getAssistiveTechnology() {
        return assistiveTechnology;
    }

    public void setAssistiveTechnology(String assistiveTechnology) {
        this.assistiveTechnology = assistiveTechnology;
    }

    public String getAccessibilityNotes() {
        return accessibilityNotes;
    }

    public void setAccessibilityNotes(String accessibilityNotes) {
        this.accessibilityNotes = accessibilityNotes;
    }
}
"@

Write-JavaFile -Path $studentDtoPath -Content $studentDtoContent

# ===================================================
# 2. adaptive-education-service: reconstruccion total
# ===================================================

$adaptiveRoot = Join-Path $root "adaptive-education-service\src\main\java\com\inclusive\adaptiveeducationservice"

if (-not (Test-Path $adaptiveRoot)) {
    throw "No se encontro la ruta: $adaptiveRoot"
}

# Eliminamos versiones viejas de clases Student-related para evitar duplicados
$filesToDelete = @(
    "entity\model\Student.java",
    "entity\model\StudentMapper.java",
    "entity\dto\StudentDTO.java",
    "entity\repository\StudentRepository.java",
    "entity\service\StudentService.java",
    "entity\service\impl\StudentServiceImpl.java",
    "entity\controller\StudentController.java",
    "client\StudentClient.java",
    "service\StudentIntegrationService.java",
    "controller\StudentProxyController.java",
    "config\DataInitializer.java"
)

foreach ($rel in $filesToDelete) {
    $full = Join-Path $adaptiveRoot $rel
    if (Test-Path $full) {
        Remove-Item $full -Force
        Write-Host "Removed old $rel" -ForegroundColor Yellow
    }
}

# Creamos estructuras de carpetas necesarias
$pathsToEnsure = @(
    "entity\model",
    "entity\repository",
    "entity\service",
    "entity\service\impl",
    "entity\controller",
    "client",
    "service",
    "controller",
    "config"
)

foreach ($rel in $pathsToEnsure) {
    $p = Join-Path $adaptiveRoot $rel
    if (-not (Test-Path $p)) {
        New-Item -ItemType Directory -Path $p -Force | Out-Null
        Write-Host "Created directory $p" -ForegroundColor Cyan
    }
}

# 2.1 Clase principal: AdaptiveEducationServiceApplication

$appPath = Join-Path $adaptiveRoot "AdaptiveEducationServiceApplication.java"
$appContent = @"
package com.inclusive.adaptiveeducationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AdaptiveEducationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdaptiveEducationServiceApplication.class, args);
    }
}
"@

Write-JavaFile -Path $appPath -Content $appContent

# 2.2 Entidad Student (accesible y rica para ML)

$studentEntityPath = Join-Path $adaptiveRoot "entity\model\Student.java"
$studentEntityContent = @"
package com.inclusive.adaptiveeducationservice.entity.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Identity
    @Column(nullable = false, length = 100)
    private String firstName;

    @Column(nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    // Basic academic info
    private Integer age;

    @Column(length = 50)
    private String grade;

    // Accessibility and learning preferences
    @Column(length = 50)
    private String preferredModality;

    private Boolean screenReaderUser;
    private Boolean signLanguageUser;
    private Boolean highContrastRequired;
    private Boolean captionsRequired;
    private Boolean keyboardNavigationPreferred;

    @Column(length = 255)
    private String assistiveTechnology;

    @Column(length = 500)
    private String accessibilityNotes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getPreferredModality() {
        return preferredModality;
    }

    public void setPreferredModality(String preferredModality) {
        this.preferredModality = preferredModality;
    }

    public Boolean getScreenReaderUser() {
        return screenReaderUser;
    }

    public void setScreenReaderUser(Boolean screenReaderUser) {
        this.screenReaderUser = screenReaderUser;
    }

    public Boolean getSignLanguageUser() {
        return signLanguageUser;
    }

    public void setSignLanguageUser(Boolean signLanguageUser) {
        this.signLanguageUser = signLanguageUser;
    }

    public Boolean getHighContrastRequired() {
        return highContrastRequired;
    }

    public void setHighContrastRequired(Boolean highContrastRequired) {
        this.highContrastRequired = highContrastRequired;
    }

    public Boolean getCaptionsRequired() {
        return captionsRequired;
    }

    public void setCaptionsRequired(Boolean captionsRequired) {
        this.captionsRequired = captionsRequired;
    }

    public Boolean getKeyboardNavigationPreferred() {
        return keyboardNavigationPreferred;
    }

    public void setKeyboardNavigationPreferred(Boolean keyboardNavigationPreferred) {
        this.keyboardNavigationPreferred = keyboardNavigationPreferred;
    }

    public String getAssistiveTechnology() {
        return assistiveTechnology;
    }

    public void setAssistiveTechnology(String assistiveTechnology) {
        this.assistiveTechnology = assistiveTechnology;
    }

    public String getAccessibilityNotes() {
        return accessibilityNotes;
    }

    public void setAccessibilityNotes(String accessibilityNotes) {
        this.accessibilityNotes = accessibilityNotes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
"@

Write-JavaFile -Path $studentEntityPath -Content $studentEntityContent

# 2.3 StudentRepository

$studentRepoPath = Join-Path $adaptiveRoot "entity\repository\StudentRepository.java"
$studentRepoContent = @"
package com.inclusive.adaptiveeducationservice.entity.repository;

import com.inclusive.adaptiveeducationservice.entity.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {

    boolean existsByEmail(String email);
}
"@

Write-JavaFile -Path $studentRepoPath -Content $studentRepoContent

# 2.4 StudentMapper (entre entidad y StudentDTO de commons-service)

$studentMapperPath = Join-Path $adaptiveRoot "entity\model\StudentMapper.java"
$studentMapperContent = @"
package com.inclusive.adaptiveeducationservice.entity.model;

import com.inclusive.common.dto.StudentDTO;

public final class StudentMapper {

    private StudentMapper() {
    }

    public static StudentDTO toDto(Student student) {
        if (student == null) {
            return null;
        }
        StudentDTO dto = new StudentDTO();
        dto.setId(student.getId());
        dto.setFirstName(student.getFirstName());
        dto.setLastName(student.getLastName());
        dto.setEmail(student.getEmail());
        dto.setAge(student.getAge());
        dto.setGrade(student.getGrade());
        dto.setPreferredModality(student.getPreferredModality());
        dto.setScreenReaderUser(student.getScreenReaderUser());
        dto.setSignLanguageUser(student.getSignLanguageUser());
        dto.setHighContrastRequired(student.getHighContrastRequired());
        dto.setCaptionsRequired(student.getCaptionsRequired());
        dto.setKeyboardNavigationPreferred(student.getKeyboardNavigationPreferred());
        dto.setAssistiveTechnology(student.getAssistiveTechnology());
        dto.setAccessibilityNotes(student.getAccessibilityNotes());
        return dto;
    }

    public static Student toEntity(StudentDTO dto) {
        if (dto == null) {
            return null;
        }
        Student student = new Student();
        student.setId(dto.getId());
        student.setFirstName(dto.getFirstName());
        student.setLastName(dto.getLastName());
        student.setEmail(dto.getEmail());
        student.setAge(dto.getAge());
        student.setGrade(dto.getGrade());
        student.setPreferredModality(dto.getPreferredModality());
        student.setScreenReaderUser(dto.getScreenReaderUser());
        student.setSignLanguageUser(dto.getSignLanguageUser());
        student.setHighContrastRequired(dto.getHighContrastRequired());
        student.setCaptionsRequired(dto.getCaptionsRequired());
        student.setKeyboardNavigationPreferred(dto.getKeyboardNavigationPreferred());
        student.setAssistiveTechnology(dto.getAssistiveTechnology());
        student.setAccessibilityNotes(dto.getAccessibilityNotes());
        return student;
    }
}
"@

Write-JavaFile -Path $studentMapperPath -Content $studentMapperContent

# 2.5 StudentService interface

$studentServicePath = Join-Path $adaptiveRoot "entity\service\StudentService.java"
$studentServiceContent = @"
package com.inclusive.adaptiveeducationservice.entity.service;

import com.inclusive.common.dto.StudentDTO;

import java.util.List;
import java.util.Optional;

public interface StudentService {

    List<StudentDTO> findAll();

    Optional<StudentDTO> findById(Long id);

    StudentDTO create(StudentDTO dto);

    Optional<StudentDTO> update(Long id, StudentDTO dto);

    void delete(Long id);
}
"@

Write-JavaFile -Path $studentServicePath -Content $studentServiceContent

# 2.6 StudentServiceImpl

$studentServiceImplPath = Join-Path $adaptiveRoot "entity\service\impl\StudentServiceImpl.java"
$studentServiceImplContent = @"
package com.inclusive.adaptiveeducationservice.entity.service.impl;

import com.inclusive.adaptiveeducationservice.entity.model.Student;
import com.inclusive.adaptiveeducationservice.entity.model.StudentMapper;
import com.inclusive.adaptiveeducationservice.entity.repository.StudentRepository;
import com.inclusive.adaptiveeducationservice.entity.service.StudentService;
import com.inclusive.common.dto.StudentDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentRepository repository;

    public StudentServiceImpl(StudentRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(StudentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StudentDTO> findById(Long id) {
        return repository.findById(id)
                .map(StudentMapper::toDto);
    }

    @Override
    public StudentDTO create(StudentDTO dto) {
        if (repository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + dto.getEmail());
        }
        Student entity = StudentMapper.toEntity(dto);
        entity.setId(null);
        Student saved = repository.save(entity);
        return StudentMapper.toDto(saved);
    }

    @Override
    public Optional<StudentDTO> update(Long id, StudentDTO dto) {
        return repository.findById(id).map(existing -> {
            existing.setFirstName(dto.getFirstName());
            existing.setLastName(dto.getLastName());
            existing.setEmail(dto.getEmail());
            existing.setAge(dto.getAge());
            existing.setGrade(dto.getGrade());
            existing.setPreferredModality(dto.getPreferredModality());
            existing.setScreenReaderUser(dto.getScreenReaderUser());
            existing.setSignLanguageUser(dto.getSignLanguageUser());
            existing.setHighContrastRequired(dto.getHighContrastRequired());
            existing.setCaptionsRequired(dto.getCaptionsRequired());
            existing.setKeyboardNavigationPreferred(dto.getKeyboardNavigationPreferred());
            existing.setAssistiveTechnology(dto.getAssistiveTechnology());
            existing.setAccessibilityNotes(dto.getAccessibilityNotes());
            Student saved = repository.save(existing);
            return StudentMapper.toDto(saved);
        });
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
"@

Write-JavaFile -Path $studentServiceImplPath -Content $studentServiceImplContent

# 2.7 StudentController (REST accesible)

$studentControllerPath = Join-Path $adaptiveRoot "entity\controller\StudentController.java"
$studentControllerContent = @"
package com.inclusive.adaptiveeducationservice.entity.controller;

import com.inclusive.adaptiveeducationservice.entity.service.StudentService;
import com.inclusive.common.dto.StudentDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/adaptive/students")
public class StudentController {

    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    @GetMapping
    public List<StudentDTO> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<StudentDTO> create(@RequestBody StudentDTO dto) {
        StudentDTO created = service.create(dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentDTO> update(
            @PathVariable Long id,
            @RequestBody StudentDTO dto) {
        return service.update(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
"@

Write-JavaFile -Path $studentControllerPath -Content $studentControllerContent

# 2.8 DataInitializer (semilla para la base de datos)

$dataInitPath = Join-Path $adaptiveRoot "config\DataInitializer.java"
$dataInitContent = @"
package com.inclusive.adaptiveeducationservice.config;

import com.inclusive.adaptiveeducationservice.entity.model.Student;
import com.inclusive.adaptiveeducationservice.entity.repository.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner loadStudents(StudentRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                for (int i = 1; i <= 10; i++) {
                    Student s = new Student();
                    s.setFirstName("Student" + i);
                    s.setLastName("Example");
                    s.setEmail("student" + i + "@example.com");
                    s.setAge(10 + i);
                    s.setGrade("Grade " + (i % 6 + 1));
                    s.setPreferredModality("visual");
                    s.setScreenReaderUser(Boolean.FALSE);
                    s.setSignLanguageUser(Boolean.FALSE);
                    s.setHighContrastRequired(Boolean.FALSE);
                    s.setCaptionsRequired(Boolean.TRUE);
                    s.setKeyboardNavigationPreferred(Boolean.TRUE);
                    s.setAssistiveTechnology(null);
                    s.setAccessibilityNotes("Sample generated student " + i);
                    repository.save(s);
                }
                System.out.println("Seeded 10 sample students in PostgreSQL.");
            } else {
                System.out.println("Students already present. No seed executed.");
            }
        };
    }
}
"@

Write-JavaFile -Path $dataInitPath -Content $dataInitContent

# 2.9 StudentClient (fachada interna, sin Feign)

$studentClientPath = Join-Path $adaptiveRoot "client\StudentClient.java"
$studentClientContent = @"
package com.inclusive.adaptiveeducationservice.client;

import com.inclusive.adaptiveeducationservice.entity.service.StudentService;
import com.inclusive.common.dto.StudentDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Internal client that wraps StudentService.
 * Later this can be replaced by an external HTTP client if needed.
 */
@Component
public class StudentClient {

    private final StudentService studentService;

    public StudentClient(StudentService studentService) {
        this.studentService = studentService;
    }

    public List<StudentDTO> getAllStudents() {
        return studentService.findAll();
    }

    public Optional<StudentDTO> getStudentById(Long id) {
        return studentService.findById(id);
    }

    public StudentDTO createStudent(StudentDTO dto) {
        return studentService.create(dto);
    }

    public Optional<StudentDTO> updateStudent(Long id, StudentDTO dto) {
        return studentService.update(id, dto);
    }

    public void deleteStudent(Long id) {
        studentService.delete(id);
    }
}
"@

Write-JavaFile -Path $studentClientPath -Content $studentClientContent

# 2.10 StudentIntegrationService

$studentIntegrationServicePath = Join-Path $adaptiveRoot "service\StudentIntegrationService.java"
$studentIntegrationServiceContent = @"
package com.inclusive.adaptiveeducationservice.service;

import com.inclusive.adaptiveeducationservice.client.StudentClient;
import com.inclusive.common.dto.StudentDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service layer dedicated to integration use cases.
 * For now it delegates to internal StudentClient, but it is ready
 * to be extended with cross-service logic, analytics and dataset export.
 */
@Service
public class StudentIntegrationService {

    private final StudentClient client;

    public StudentIntegrationService(StudentClient client) {
        this.client = client;
    }

    public List<StudentDTO> getAllStudents() {
        return client.getAllStudents();
    }

    public Optional<StudentDTO> getStudentById(Long id) {
        return client.getStudentById(id);
    }

    public StudentDTO createStudent(StudentDTO dto) {
        return client.createStudent(dto);
    }

    public Optional<StudentDTO> updateStudent(Long id, StudentDTO dto) {
        return client.updateStudent(id, dto);
    }

    public void deleteStudent(Long id) {
        client.deleteStudent(id);
    }
}
"@

Write-JavaFile -Path $studentIntegrationServicePath -Content $studentIntegrationServiceContent

# 2.11 StudentProxyController (fachada de integracion)

$studentProxyControllerPath = Join-Path $adaptiveRoot "controller\StudentProxyController.java"
$studentProxyControllerContent = @"
package com.inclusive.adaptiveeducationservice.controller;

import com.inclusive.adaptiveeducationservice.service.StudentIntegrationService;
import com.inclusive.common.dto.StudentDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/integration/students")
public class StudentProxyController {

    private final StudentIntegrationService service;

    public StudentProxyController(StudentIntegrationService service) {
        this.service = service;
    }

    @GetMapping
    public List<StudentDTO> getAll() {
        return service.getAllStudents();
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getById(@PathVariable Long id) {
        return service.getStudentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<StudentDTO> create(@RequestBody StudentDTO dto) {
        StudentDTO created = service.createStudent(dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentDTO> update(
            @PathVariable Long id,
            @RequestBody StudentDTO dto) {
        return service.updateStudent(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
}
"@

Write-JavaFile -Path $studentProxyControllerPath -Content $studentProxyControllerContent

# ===========================================
# 3. Verificar dependencia commons-service en pom
# ===========================================

$pomPath = Join-Path $root "adaptive-education-service\pom.xml"
if (Test-Path $pomPath) {
    $pomText = Get-Content $pomPath -Raw
    if ($pomText -notmatch "<artifactId>commons-service</artifactId>") {
        Write-Host ""
        Write-Host "WARNING: adaptive-education-service/pom.xml no tiene la dependencia a commons-service." -ForegroundColor Yellow
        Write-Host "Agrega dentro de <dependencies> este bloque:" -ForegroundColor Yellow
        Write-Host ""
        Write-Host "  <dependency>" -ForegroundColor Yellow
        Write-Host "      <groupId>com.inclusive.platform</groupId>" -ForegroundColor Yellow
        Write-Host "      <artifactId>commons-service</artifactId>" -ForegroundColor Yellow
        Write-Host "      <version>\${project.version}</version>" -ForegroundColor Yellow
        Write-Host "  </dependency>" -ForegroundColor Yellow
        Write-Host ""
    } else {
        Write-Host "Dependency commons-service already present in adaptive-education-service/pom.xml" -ForegroundColor Green
    }
} else {
    Write-Host "WARNING: No se encontro adaptive-education-service/pom.xml" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host " REBUILD COMPLETADO." -ForegroundColor Cyan
Write-Host " Ahora ejecuta: mvn clean package -pl adaptive-education-service -am" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
