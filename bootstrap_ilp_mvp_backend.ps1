param(
    [string]$RootPath = (Get-Location).Path
)

$ErrorActionPreference = "Stop"

function Write-Info($msg) {
    Write-Host "[INFO] $msg" -ForegroundColor Cyan
}

function Write-Warn($msg) {
    Write-Host "[WARN] $msg" -ForegroundColor Yellow
}

function Write-Ok($msg) {
    Write-Host "[OK]   $msg" -ForegroundColor Green
}

function Ensure-Dir($path) {
    if (-not (Test-Path $path)) {
        New-Item -ItemType Directory -Path $path | Out-Null
        Write-Info "Created directory: $path"
    }
}

function Write-AsciiFile($path, $content) {
    $dir = Split-Path $path -Parent
    Ensure-Dir $dir
    $content | Set-Content -Path $path -Encoding ASCII
    Write-Ok "Wrote file: $path"
}

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host " BOOTSTRAP ILP MVP BACKEND (CORE STUDENT)" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan

# --------------------------------------------------------------------------------------------------
# 1. commons-service: StudentDTO
# --------------------------------------------------------------------------------------------------

$commonsDtoDir = Join-Path $RootPath "commons-service\src\main\java\com\inclusive\common\dto"
Ensure-Dir $commonsDtoDir
$commonsStudentDtoPath = Join-Path $commonsDtoDir "StudentDTO.java"

$commonsStudentDtoContent = @'
package com.inclusive.common.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * Shared Student DTO for cross-service communication in the ILP platform.
 * This DTO is intentionally stable and normalized to be used by multiple microservices.
 */
public class StudentDTO {

    private Long id;
    private Long userId;

    private String fullName;
    private String email;
    private String gender;
    private LocalDate birthDate;

    // Accessibility and inclusion
    private String disabilityStatus;
    private Boolean needsAssistiveTechnology;

    // Learning styles and profiles
    private String learningStyleFelder;
    private String learningStyleKolb;
    private String vocationalProfileKuder;
    private String adaptiveContentProfile;
    private String recommendedLearningPath;
    private String preferredContentFormat;
    private String preferredStudyTime;

    // Academic and risk features for ML
    private Double attendanceRate;
    private Double averageGrade;
    private Double predictedDropoutRisk;

    // Account and metadata
    private String accountStatus;
    private String schoolLevel;
    private String socioEconomicStatus;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public StudentDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getDisabilityStatus() {
        return disabilityStatus;
    }

    public void setDisabilityStatus(String disabilityStatus) {
        this.disabilityStatus = disabilityStatus;
    }

    public Boolean getNeedsAssistiveTechnology() {
        return needsAssistiveTechnology;
    }

    public void setNeedsAssistiveTechnology(Boolean needsAssistiveTechnology) {
        this.needsAssistiveTechnology = needsAssistiveTechnology;
    }

    public String getLearningStyleFelder() {
        return learningStyleFelder;
    }

    public void setLearningStyleFelder(String learningStyleFelder) {
        this.learningStyleFelder = learningStyleFelder;
    }

    public String getLearningStyleKolb() {
        return learningStyleKolb;
    }

    public void setLearningStyleKolb(String learningStyleKolb) {
        this.learningStyleKolb = learningStyleKolb;
    }

    public String getVocationalProfileKuder() {
        return vocationalProfileKuder;
    }

    public void setVocationalProfileKuder(String vocationalProfileKuder) {
        this.vocationalProfileKuder = vocationalProfileKuder;
    }

    public String getAdaptiveContentProfile() {
        return adaptiveContentProfile;
    }

    public void setAdaptiveContentProfile(String adaptiveContentProfile) {
        this.adaptiveContentProfile = adaptiveContentProfile;
    }

    public String getRecommendedLearningPath() {
        return recommendedLearningPath;
    }

    public void setRecommendedLearningPath(String recommendedLearningPath) {
        this.recommendedLearningPath = recommendedLearningPath;
    }

    public String getPreferredContentFormat() {
        return preferredContentFormat;
    }

    public void setPreferredContentFormat(String preferredContentFormat) {
        this.preferredContentFormat = preferredContentFormat;
    }

    public String getPreferredStudyTime() {
        return preferredStudyTime;
    }

    public void setPreferredStudyTime(String preferredStudyTime) {
        this.preferredStudyTime = preferredStudyTime;
    }

    public Double getAttendanceRate() {
        return attendanceRate;
    }

    public void setAttendanceRate(Double attendanceRate) {
        this.attendanceRate = attendanceRate;
    }

    public Double getAverageGrade() {
        return averageGrade;
    }

    public void setAverageGrade(Double averageGrade) {
        this.averageGrade = averageGrade;
    }

    public Double getPredictedDropoutRisk() {
        return predictedDropoutRisk;
    }

    public void setPredictedDropoutRisk(Double predictedDropoutRisk) {
        this.predictedDropoutRisk = predictedDropoutRisk;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getSchoolLevel() {
        return schoolLevel;
    }

    public void setSchoolLevel(String schoolLevel) {
        this.schoolLevel = schoolLevel;
    }

    public String getSocioEconomicStatus() {
        return socioEconomicStatus;
    }

    public void setSocioEconomicStatus(String socioEconomicStatus) {
        this.socioEconomicStatus = socioEconomicStatus;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
'@

Write-AsciiFile $commonsStudentDtoPath $commonsStudentDtoContent

# --------------------------------------------------------------------------------------------------
# 2. adaptive-education-service: Student entity and repo/service/mapper/controller/config
# --------------------------------------------------------------------------------------------------

$adaptiveBase = Join-Path $RootPath "adaptive-education-service\src\main\java\com\inclusive\adaptiveeducationservice"

$entityModelDir      = Join-Path $adaptiveBase "entity\model"
$entityRepoDir       = Join-Path $adaptiveBase "entity\repository"
$entityServiceDir    = Join-Path $adaptiveBase "entity\service"
$entityServiceImplDir= Join-Path $adaptiveBase "entity\service\impl"
$entityControllerDir = Join-Path $adaptiveBase "entity\controller"
$configDir           = Join-Path $adaptiveBase "config"
$serviceDir          = Join-Path $adaptiveBase "service"
$controllerDir       = Join-Path $adaptiveBase "controller"
$clientDir           = Join-Path $adaptiveBase "client"

Ensure-Dir $entityModelDir
Ensure-Dir $entityRepoDir
Ensure-Dir $entityServiceDir
Ensure-Dir $entityServiceImplDir
Ensure-Dir $entityControllerDir
Ensure-Dir $configDir
Ensure-Dir $serviceDir
Ensure-Dir $controllerDir
Ensure-Dir $clientDir

# 2.1 Student entity (normalized but mapped to students table)

$studentEntityPath = Join-Path $entityModelDir "Student.java"
$studentEntityContent = @'
package com.inclusive.adaptiveeducationservice.entity.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * Core Student entity mapped to public.students.
 * Designed to be rich enough for ML and accessibility use cases.
 */
@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "gender", length = 50)
    private String gender;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    // Accessibility and inclusion
    @Column(name = "disability_status", length = 100)
    private String disabilityStatus;

    @Column(name = "needs_assistive_technology")
    private Boolean needsAssistiveTechnology;

    // Learning styles and profiles
    @Column(name = "learning_style_felder")
    private String learningStyleFelder;

    @Column(name = "learning_style_kolb")
    private String learningStyleKolb;

    @Column(name = "vocational_profile_kuder")
    private String vocationalProfileKuder;

    @Column(name = "adaptive_content_profile")
    private String adaptiveContentProfile;

    @Column(name = "recommended_learning_path")
    private String recommendedLearningPath;

    @Column(name = "preferred_content_format")
    private String preferredContentFormat;

    @Column(name = "preferred_study_time")
    private String preferredStudyTime;

    // Academic and risk
    @Column(name = "attendance_rate")
    private Double attendanceRate;

    @Column(name = "average_grade")
    private Double averageGrade;

    @Column(name = "predicted_dropout_risk")
    private Double predictedDropoutRisk;

    // Socio-economic and context
    @Column(name = "school_level")
    private String schoolLevel;

    @Column(name = "socio_economic_status")
    private String socioEconomicStatus;

    // Device and connectivity
    @Column(name = "device_access")
    private String deviceAccess;

    @Column(name = "internet_access")
    private String internetAccess;

    // Account and audit
    @Column(name = "account_status")
    private String accountStatus;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    public Student() {
    }

    @PrePersist
    public void prePersist() {
        OffsetDateTime now = OffsetDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getDisabilityStatus() {
        return disabilityStatus;
    }

    public void setDisabilityStatus(String disabilityStatus) {
        this.disabilityStatus = disabilityStatus;
    }

    public Boolean getNeedsAssistiveTechnology() {
        return needsAssistiveTechnology;
    }

    public void setNeedsAssistiveTechnology(Boolean needsAssistiveTechnology) {
        this.needsAssistiveTechnology = needsAssistiveTechnology;
    }

    public String getLearningStyleFelder() {
        return learningStyleFelder;
    }

    public void setLearningStyleFelder(String learningStyleFelder) {
        this.learningStyleFelder = learningStyleFelder;
    }

    public String getLearningStyleKolb() {
        return learningStyleKolb;
    }

    public void setLearningStyleKolb(String learningStyleKolb) {
        this.learningStyleKolb = learningStyleKolb;
    }

    public String getVocationalProfileKuder() {
        return vocationalProfileKuder;
    }

    public void setVocationalProfileKuder(String vocationalProfileKuder) {
        this.vocationalProfileKuder = vocationalProfileKuder;
    }

    public String getAdaptiveContentProfile() {
        return adaptiveContentProfile;
    }

    public void setAdaptiveContentProfile(String adaptiveContentProfile) {
        this.adaptiveContentProfile = adaptiveContentProfile;
    }

    public String getRecommendedLearningPath() {
        return recommendedLearningPath;
    }

    public void setRecommendedLearningPath(String recommendedLearningPath) {
        this.recommendedLearningPath = recommendedLearningPath;
    }

    public String getPreferredContentFormat() {
        return preferredContentFormat;
    }

    public void setPreferredContentFormat(String preferredContentFormat) {
        this.preferredContentFormat = preferredContentFormat;
    }

    public String getPreferredStudyTime() {
        return preferredStudyTime;
    }

    public void setPreferredStudyTime(String preferredStudyTime) {
        this.preferredStudyTime = preferredStudyTime;
    }

    public Double getAttendanceRate() {
        return attendanceRate;
    }

    public void setAttendanceRate(Double attendanceRate) {
        this.attendanceRate = attendanceRate;
    }

    public Double getAverageGrade() {
        return averageGrade;
    }

    public void setAverageGrade(Double averageGrade) {
        this.averageGrade = averageGrade;
    }

    public Double getPredictedDropoutRisk() {
        return predictedDropoutRisk;
    }

    public void setPredictedDropoutRisk(Double predictedDropoutRisk) {
        this.predictedDropoutRisk = predictedDropoutRisk;
    }

    public String getSchoolLevel() {
        return schoolLevel;
    }

    public void setSchoolLevel(String schoolLevel) {
        this.schoolLevel = schoolLevel;
    }

    public String getSocioEconomicStatus() {
        return socioEconomicStatus;
    }

    public void setSocioEconomicStatus(String socioEconomicStatus) {
        this.socioEconomicStatus = socioEconomicStatus;
    }

    public String getDeviceAccess() {
        return deviceAccess;
    }

    public void setDeviceAccess(String deviceAccess) {
        this.deviceAccess = deviceAccess;
    }

    public String getInternetAccess() {
        return internetAccess;
    }

    public void setInternetAccess(String internetAccess) {
        this.internetAccess = internetAccess;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
'@

Write-AsciiFile $studentEntityPath $studentEntityContent

# 2.2 StudentRepository

$studentRepoPath = Join-Path $entityRepoDir "StudentRepository.java"
$studentRepoContent = @'
package com.inclusive.adaptiveeducationservice.entity.repository;

import com.inclusive.adaptiveeducationservice.entity.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByUserId(Long userId);

    boolean existsByEmail(String email);
}
'@
Write-AsciiFile $studentRepoPath $studentRepoContent

# 2.3 StudentMapper

$studentMapperPath = Join-Path $entityModelDir "StudentMapper.java"
$studentMapperContent = @'
package com.inclusive.adaptiveeducationservice.entity.model;

import com.inclusive.common.dto.StudentDTO;

public final class StudentMapper {

    private StudentMapper() {
    }

    public static StudentDTO toDto(Student entity) {
        if (entity == null) {
            return null;
        }
        StudentDTO dto = new StudentDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUserId());
        dto.setFullName(entity.getFullName());
        dto.setEmail(entity.getEmail());
        dto.setGender(entity.getGender());
        dto.setBirthDate(entity.getBirthDate());
        dto.setDisabilityStatus(entity.getDisabilityStatus());
        dto.setNeedsAssistiveTechnology(entity.getNeedsAssistiveTechnology());
        dto.setLearningStyleFelder(entity.getLearningStyleFelder());
        dto.setLearningStyleKolb(entity.getLearningStyleKolb());
        dto.setVocationalProfileKuder(entity.getVocationalProfileKuder());
        dto.setAdaptiveContentProfile(entity.getAdaptiveContentProfile());
        dto.setRecommendedLearningPath(entity.getRecommendedLearningPath());
        dto.setPreferredContentFormat(entity.getPreferredContentFormat());
        dto.setPreferredStudyTime(entity.getPreferredStudyTime());
        dto.setAttendanceRate(entity.getAttendanceRate());
        dto.setAverageGrade(entity.getAverageGrade());
        dto.setPredictedDropoutRisk(entity.getPredictedDropoutRisk());
        dto.setSchoolLevel(entity.getSchoolLevel());
        dto.setSocioEconomicStatus(entity.getSocioEconomicStatus());
        dto.setAccountStatus(entity.getAccountStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    public static Student toEntity(StudentDTO dto) {
        if (dto == null) {
            return null;
        }
        Student entity = new Student();
        entity.setId(dto.getId());
        entity.setUserId(dto.getUserId());
        entity.setFullName(dto.getFullName());
        entity.setEmail(dto.getEmail());
        entity.setGender(dto.getGender());
        entity.setBirthDate(dto.getBirthDate());
        entity.setDisabilityStatus(dto.getDisabilityStatus());
        entity.setNeedsAssistiveTechnology(dto.getNeedsAssistiveTechnology());
        entity.setLearningStyleFelder(dto.getLearningStyleFelder());
        entity.setLearningStyleKolb(dto.getLearningStyleKolb());
        entity.setVocationalProfileKuder(dto.getVocationalProfileKuder());
        entity.setAdaptiveContentProfile(dto.getAdaptiveContentProfile());
        entity.setRecommendedLearningPath(dto.getRecommendedLearningPath());
        entity.setPreferredContentFormat(dto.getPreferredContentFormat());
        entity.setPreferredStudyTime(dto.getPreferredStudyTime());
        entity.setAttendanceRate(dto.getAttendanceRate());
        entity.setAverageGrade(dto.getAverageGrade());
        entity.setPredictedDropoutRisk(dto.getPredictedDropoutRisk());
        entity.setSchoolLevel(dto.getSchoolLevel());
        entity.setSocioEconomicStatus(dto.getSocioEconomicStatus());
        entity.setAccountStatus(dto.getAccountStatus());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());
        return entity;
    }
}
'@
Write-AsciiFile $studentMapperPath $studentMapperContent

# 2.4 StudentService interface

$studentServicePath = Join-Path $entityServiceDir "StudentService.java"
$studentServiceContent = @'
package com.inclusive.adaptiveeducationservice.entity.service;

import com.inclusive.common.dto.StudentDTO;

import java.util.List;
import java.util.Optional;

public interface StudentService {

    List<StudentDTO> findAll();

    Optional<StudentDTO> findById(Long id);

    Optional<StudentDTO> findByUserId(Long userId);

    StudentDTO create(StudentDTO student);

    StudentDTO update(Long id, StudentDTO student);

    void delete(Long id);
}
'@
Write-AsciiFile $studentServicePath $studentServiceContent

# 2.5 StudentServiceImpl

$studentServiceImplPath = Join-Path $entityServiceImplDir "StudentServiceImpl.java"
$studentServiceImplContent = @'
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
        return repository.findById(id).map(StudentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StudentDTO> findByUserId(Long userId) {
        return repository.findByUserId(userId).map(StudentMapper::toDto);
    }

    @Override
    public StudentDTO create(StudentDTO dto) {
        if (dto.getEmail() != null && repository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }
        Student entity = StudentMapper.toEntity(dto);
        Student saved = repository.save(entity);
        return StudentMapper.toDto(saved);
    }

    @Override
    public StudentDTO update(Long id, StudentDTO dto) {
        Student entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with id " + id));
        // Update mutable fields only
        entity.setFullName(dto.getFullName());
        entity.setEmail(dto.getEmail());
        entity.setGender(dto.getGender());
        entity.setBirthDate(dto.getBirthDate());
        entity.setDisabilityStatus(dto.getDisabilityStatus());
        entity.setNeedsAssistiveTechnology(dto.getNeedsAssistiveTechnology());
        entity.setLearningStyleFelder(dto.getLearningStyleFelder());
        entity.setLearningStyleKolb(dto.getLearningStyleKolb());
        entity.setVocationalProfileKuder(dto.getVocationalProfileKuder());
        entity.setAdaptiveContentProfile(dto.getAdaptiveContentProfile());
        entity.setRecommendedLearningPath(dto.getRecommendedLearningPath());
        entity.setPreferredContentFormat(dto.getPreferredContentFormat());
        entity.setPreferredStudyTime(dto.getPreferredStudyTime());
        entity.setAttendanceRate(dto.getAttendanceRate());
        entity.setAverageGrade(dto.getAverageGrade());
        entity.setPredictedDropoutRisk(dto.getPredictedDropoutRisk());
        entity.setSchoolLevel(dto.getSchoolLevel());
        entity.setSocioEconomicStatus(dto.getSocioEconomicStatus());
        entity.setDeviceAccess(dto.getAccountStatus());
        entity.setAccountStatus(dto.getAccountStatus());
        Student saved = repository.save(entity);
        return StudentMapper.toDto(saved);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
'@
Write-AsciiFile $studentServiceImplPath $studentServiceImplContent

# 2.6 REST StudentController

$studentControllerPath = Join-Path $entityControllerDir "StudentController.java"
$studentControllerContent = @'
package com.inclusive.adaptiveeducationservice.entity.controller;

import com.inclusive.adaptiveeducationservice.entity.service.StudentService;
import com.inclusive.common.dto.StudentDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/students")
@CrossOrigin(origins = "*")
public class StudentController {

    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<StudentDTO>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<StudentDTO> getByUserId(@PathVariable Long userId) {
        return service.findByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<StudentDTO> create(@RequestBody StudentDTO dto) {
        StudentDTO created = service.create(dto);
        return ResponseEntity.created(URI.create("/api/v1/students/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentDTO> update(@PathVariable Long id, @RequestBody StudentDTO dto) {
        StudentDTO updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
'@
Write-AsciiFile $studentControllerPath $studentControllerContent

# 2.7 DataInitializer

$dataInitPath = Join-Path $configDir "DataInitializer.java"
$dataInitContent = @'
package com.inclusive.adaptiveeducationservice.config;

import com.inclusive.adaptiveeducationservice.entity.model.Student;
import com.inclusive.adaptiveeducationservice.entity.repository.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initStudents(StudentRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                return;
            }
            for (int i = 1; i <= 10; i++) {
                Student s = new Student();
                s.setUserId((long) i);
                s.setFullName("Demo Student " + i);
                s.setEmail("student" + i + "@demo.local");
                s.setGender("UNSPECIFIED");
                s.setDisabilityStatus("UNKNOWN");
                s.setNeedsAssistiveTechnology(Boolean.FALSE);
                s.setLearningStyleFelder("UNDEFINED");
                s.setLearningStyleKolb("UNDEFINED");
                s.setVocationalProfileKuder("UNDEFINED");
                s.setAdaptiveContentProfile("DEFAULT");
                s.setRecommendedLearningPath("DEFAULT");
                s.setPreferredContentFormat("MIXED");
                s.setPreferredStudyTime("ANY");
                s.setAttendanceRate(0.85d);
                s.setAverageGrade(3.5d);
                s.setPredictedDropoutRisk(0.2d);
                s.setSchoolLevel("SECONDARY");
                s.setSocioEconomicStatus("UNKNOWN");
                s.setDeviceAccess("SHARED");
                s.setInternetAccess("LIMITED");
                s.setAccountStatus("ACTIVE");
                repository.save(s);
            }
        };
    }
}
'@
Write-AsciiFile $dataInitPath $dataInitContent

# 2.8 Simple StudentIntegrationService and Proxy (SaaS-friendly API facade)

$studentIntegrationServicePath = Join-Path $serviceDir "StudentIntegrationService.java"
$studentIntegrationServiceContent = @'
package com.inclusive.adaptiveeducationservice.service;

import com.inclusive.common.dto.StudentDTO;

import java.util.List;
import java.util.Optional;

public interface StudentIntegrationService {

    List<StudentDTO> listStudents();

    Optional<StudentDTO> getStudent(Long id);
}
'@
Write-AsciiFile $studentIntegrationServicePath $studentIntegrationServiceContent

$studentIntegrationServiceImplPath = Join-Path $serviceDir "StudentIntegrationServiceImpl.java"
$studentIntegrationServiceImplContent = @'
package com.inclusive.adaptiveeducationservice.service;

import com.inclusive.adaptiveeducationservice.entity.service.StudentService;
import com.inclusive.common.dto.StudentDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentIntegrationServiceImpl implements StudentIntegrationService {

    private final StudentService studentService;

    public StudentIntegrationServiceImpl(StudentService studentService) {
        this.studentService = studentService;
    }

    @Override
    public List<StudentDTO> listStudents() {
        return studentService.findAll();
    }

    @Override
    public Optional<StudentDTO> getStudent(Long id) {
        return studentService.findById(id);
    }
}
'@
Write-AsciiFile $studentIntegrationServiceImplPath $studentIntegrationServiceImplContent

$studentProxyControllerPath = Join-Path $controllerDir "StudentProxyController.java"
$studentProxyControllerContent = @'
package com.inclusive.adaptiveeducationservice.controller;

import com.inclusive.adaptiveeducationservice.service.StudentIntegrationService;
import com.inclusive.common.dto.StudentDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/internal/students")
public class StudentProxyController {

    private final StudentIntegrationService integrationService;

    public StudentProxyController(StudentIntegrationService integrationService) {
        this.integrationService = integrationService;
    }

    @GetMapping
    public ResponseEntity<List<StudentDTO>> list() {
        return ResponseEntity.ok(integrationService.listStudents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> get(@PathVariable Long id) {
        return integrationService.getStudent(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
'@
Write-AsciiFile $studentProxyControllerPath $studentProxyControllerContent

# 2.9 Minimal StudentClient stub (for future external calls)

$studentClientPath = Join-Path $clientDir "StudentClient.java"
$studentClientContent = @'
package com.inclusive.adaptiveeducationservice.client;

/**
 * Placeholder for future HTTP client to other microservices.
 * For now, this class is intentionally empty to keep the module compiling.
 */
public class StudentClient {
}
'@
Write-AsciiFile $studentClientPath $studentClientContent

# --------------------------------------------------------------------------------------------------
# 3. POM: asegurar dependencia de adaptive-education-service a commons-service
# --------------------------------------------------------------------------------------------------

$adaptivePomPath = Join-Path $RootPath "adaptive-education-service\pom.xml"
if (Test-Path $adaptivePomPath) {
    $adaptivePom = Get-Content $adaptivePomPath -Raw

    $dependencyBlock = @'
        <dependency>
            <groupId>com.inclusive.platform</groupId>
            <artifactId>commons-service</artifactId>
            <version>1.0.0-SNAPSHOT</version>
        </dependency>
'@

    if ($adaptivePom -notmatch "commons-service") {
        Write-Info "Injecting commons-service dependency into adaptive-education-service/pom.xml"
        $adaptivePom = $adaptivePom -replace "(?s)(</dependencies>)", "$dependencyBlock`n    </dependencies>"
        $adaptivePom | Set-Content -Path $adaptivePomPath -Encoding ASCII
        Write-Ok "Updated: $adaptivePomPath"
    } else {
        Write-Warn "commons-service dependency seems to be already referenced in adaptive-education-service/pom.xml"
    }
} else {
    Write-Warn "adaptive-education-service/pom.xml not found, skipping dependency patch."
}

# --------------------------------------------------------------------------------------------------
# 4. IaC: docker-compose (Postgres + adaptive-education-service)
# --------------------------------------------------------------------------------------------------

$dockerComposePath = Join-Path $RootPath "docker-compose.yml"
$dockerComposeContent = @'
version: "3.9"

services:
  postgres:
    image: postgres:16
    container_name: ilp-postgres
    environment:
      POSTGRES_DB: ilp_db
      POSTGRES_USER: ilp_user
      POSTGRES_PASSWORD: ilp_password
    ports:
      - "5432:5432"
    volumes:
      - ilp_pgdata:/var/lib/postgresql/data

  adaptive-education-service:
    build:
      context: ./adaptive-education-service
      dockerfile: Dockerfile
    container_name: adaptive-education-service
    environment:
      SPRING_PROFILES_ACTIVE: docker
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/ilp_db
      SPRING_DATASOURCE_USERNAME: ilp_user
      SPRING_DATASOURCE_PASSWORD: ilp_password
    depends_on:
      - postgres
    ports:
      - "8081:8080"

volumes:
  ilp_pgdata:
'@
Write-AsciiFile $dockerComposePath $dockerComposeContent

# --------------------------------------------------------------------------------------------------
# 5. Dockerfile for adaptive-education-service
# --------------------------------------------------------------------------------------------------

$dockerfilePath = Join-Path $RootPath "adaptive-education-service\Dockerfile"
$dockerfileContent = @'
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /workspace/app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN ./mvnw -q -DskipTests package

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /workspace/app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app/app.jar"]
'@
Write-AsciiFile $dockerfilePath $dockerfileContent

# --------------------------------------------------------------------------------------------------
# 6. GitHub Actions CI/CD pipeline (Maven + Docker build)
# --------------------------------------------------------------------------------------------------

$githubDir = Join-Path $RootPath ".github\workflows"
Ensure-Dir $githubDir
$ciCdPath = Join-Path $githubDir "ci-cd.yml"
$ciCdContent = @'
name: ILP Backend CI

on:
  push:
    branches: [ "main", "master" ]
  pull_request:
    branches: [ "main", "master" ]

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: "17"
          distribution: "temurin"

      - name: Build with Maven
        run: mvn -B clean package

      - name: Build Docker image for adaptive-education-service
        if: success()
        run: |
          cd adaptive-education-service
          docker build -t ilp/adaptive-education-service:latest .
'@
Write-AsciiFile $ciCdPath $ciCdContent

Write-Host "==========================================" -ForegroundColor Green
Write-Host " BOOTSTRAP COMPLETED." -ForegroundColor Green
Write-Host " Now run: mvn clean package -pl adaptive-education-service -am" -ForegroundColor Green
Write-Host "==========================================" -ForegroundColor Green
