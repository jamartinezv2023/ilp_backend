# =====================================================================
#  rebuild_ilp_mvp.ps1
#  Reconstruye parte clave del backend para un MVP funcional:
#  - Limpia BOM de auth-service y adaptive-education-service
#  - Reconstruye todo el dominio Student en adaptive-education-service
#  - Crea DTO compartido en commons-service
#  - Crea clases de accesibilidad en auth-service
# =====================================================================

$root = "C:\temp_ilp\inclusive-learning-platform-backend"

Write-Host "===================================================" -ForegroundColor Cyan
Write-Host "  Reconstruyendo backend ILP para MVP accesible"     -ForegroundColor Cyan
Write-Host "  Raiz del proyecto: $root"                         -ForegroundColor Cyan
Write-Host "===================================================" -ForegroundColor Cyan

# ---------------------------------------------------------------------
# Helper: escribir archivo asegurando carpeta y usando ASCII (sin BOM)
# ---------------------------------------------------------------------
function Write-JavaFile {
    param(
        [string]$Path,
        [string]$Content
    )

    $dir = Split-Path $Path
    if (-not (Test-Path $dir)) {
        New-Item -ItemType Directory -Path $dir -Force | Out-Null
    }

    # Usamos ASCII para evitar BOM y problemas de codificación.
    $Content | Set-Content -Path $Path -Encoding ASCII
    Write-Host "  [+] Archivo Java actualizado: $Path" -ForegroundColor Green
}

# ---------------------------------------------------------------------
# 1. Limpiar BOM de todos los .java en auth-service y adaptive-education-service
# ---------------------------------------------------------------------
Write-Host ""
Write-Host "1) Limpiando BOM y normalizando codificación en auth-service y adaptive-education-service..." -ForegroundColor Yellow

$modulesToClean = @(
    "$root\auth-service\src\main\java",
    "$root\adaptive-education-service\src\main\java"
)

foreach ($m in $modulesToClean) {
    if (Test-Path $m) {
        Get-ChildItem $m -Recurse -Filter *.java | ForEach-Object {
            $txt = Get-Content $_.FullName
            $txt | Set-Content $_.FullName -Encoding ASCII
        }
        Write-Host "  [OK] Normalizados .java en: $m" -ForegroundColor Green
    } else {
        Write-Host "  [WARN] No existe ruta: $m" -ForegroundColor DarkYellow
    }
}

# ---------------------------------------------------------------------
# 2. Eliminar posibles repos duplicados de StudentRepository antiguos
# ---------------------------------------------------------------------
Write-Host ""
Write-Host "2) Eliminando StudentRepository obsoleto (si existe)..." -ForegroundColor Yellow

$oldRepo = "$root\adaptive-education-service\src\main\java\com\inclusive\adaptiveeducationservice\repository\StudentRepository.java"
if (Test-Path $oldRepo) {
    Remove-Item $oldRepo -Force
    Write-Host "  [DEL] $oldRepo" -ForegroundColor DarkYellow
} else {
    Write-Host "  [OK] No se encontró StudentRepository obsoleto en la ruta antigua." -ForegroundColor Green
}

# ---------------------------------------------------------------------
# 3. commons-service: DTO compartido para futuros escenarios ML/IA
# ---------------------------------------------------------------------
Write-Host ""
Write-Host "3) Creando DTO compartido en commons-service..." -ForegroundColor Yellow

$commonsStudentDtoPath = "$root\commons-service\src\main\java\com\inclusive\common\dto\StudentDTO.java"
$commonsStudentDtoContent = @"
package com.inclusive.common.dto;

/**
 * StudentDTO (comun) pensado para interoperabilidad entre microservicios
 * y para construir datasets de Machine Learning / Deep Learning.
 *
 * Archivo: commons-service/src/main/java/com/inclusive/common/dto/StudentDTO.java
 */
public class StudentDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Integer age;

    // Datos educativos
    private String grade;
    private String institutionCode;

    // Estilos de aprendizaje / orientacion vocacional (simplificado)
    private String kolbStyle;
    private String felderSilvermanStyle;
    private String careerInterestKuder;

    // Accesibilidad y consentimiento para investigacion
    private String disabilityType;       // VISUAL, HEARING, etc.
    private Boolean highContrastEnabled;
    private Boolean screenReaderEnabled;
    private Boolean consentForResearch;

    public StudentDTO() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public String getInstitutionCode() { return institutionCode; }
    public void setInstitutionCode(String institutionCode) { this.institutionCode = institutionCode; }

    public String getKolbStyle() { return kolbStyle; }
    public void setKolbStyle(String kolbStyle) { this.kolbStyle = kolbStyle; }

    public String getFelderSilvermanStyle() { return felderSilvermanStyle; }
    public void setFelderSilvermanStyle(String felderSilvermanStyle) { this.felderSilvermanStyle = felderSilvermanStyle; }

    public String getCareerInterestKuder() { return careerInterestKuder; }
    public void setCareerInterestKuder(String careerInterestKuder) { this.careerInterestKuder = careerInterestKuder; }

    public String getDisabilityType() { return disabilityType; }
    public void setDisabilityType(String disabilityType) { this.disabilityType = disabilityType; }

    public Boolean getHighContrastEnabled() { return highContrastEnabled; }
    public void setHighContrastEnabled(Boolean highContrastEnabled) { this.highContrastEnabled = highContrastEnabled; }

    public Boolean getScreenReaderEnabled() { return screenReaderEnabled; }
    public void setScreenReaderEnabled(Boolean screenReaderEnabled) { this.screenReaderEnabled = screenReaderEnabled; }

    public Boolean getConsentForResearch() { return consentForResearch; }
    public void setConsentForResearch(Boolean consentForResearch) { this.consentForResearch = consentForResearch; }
}
"@

Write-JavaFile -Path $commonsStudentDtoPath -Content $commonsStudentDtoContent

# ---------------------------------------------------------------------
# 4. auth-service: clases para accesibilidad y metodos de autenticacion
# ---------------------------------------------------------------------
Write-Host ""
Write-Host "4) Creando clases de accesibilidad y autenticacion en auth-service..." -ForegroundColor Yellow

$authModelDir = "$root\auth-service\src\main\java\com\inclusive\authservice\model"

# 4.1 Enum DisabilityType
$disabilityEnumPath = Join-Path $authModelDir "DisabilityType.java"
$disabilityEnumContent = @"
package com.inclusive.authservice.model;

/**
 * Enum basico para modelar tipo principal de discapacidad.
 *
 * Archivo: auth-service/src/main/java/com/inclusive/authservice/model/DisabilityType.java
 */
public enum DisabilityType {
    NONE,
    VISUAL,
    HEARING,
    MOTOR,
    COGNITIVE,
    MULTIPLE
}
"@
Write-JavaFile -Path $disabilityEnumPath -Content $disabilityEnumContent

# 4.2 Enum AuthMethodType
$authMethodEnumPath = Join-Path $authModelDir "AuthMethodType.java"
$authMethodEnumContent = @"
package com.inclusive.authservice.model;

/**
 * Enum para describir metodos de autenticacion soportados por el sistema.
 *
 * Archivo: auth-service/src/main/java/com/inclusive/authservice/model/AuthMethodType.java
 */
public enum AuthMethodType {
    PASSWORD,
    MAGIC_LINK_EMAIL,
    OTP_EMAIL,
    OTP_SMS,
    WEBAUTHN,
    OAUTH_GOOGLE,
    OAUTH_MICROSOFT
}
"@
Write-JavaFile -Path $authMethodEnumPath -Content $authMethodEnumContent

# 4.3 AccessibilityProfile
$accessibilityProfilePath = Join-Path $authModelDir "AccessibilityProfile.java"
$accessibilityProfileContent = @"
package com.inclusive.authservice.model;

import jakarta.persistence.*;

/**
 * Perfil de accesibilidad vinculado a un usuario.
 *
 * Archivo: auth-service/src/main/java/com/inclusive/authservice/model/AccessibilityProfile.java
 */
@Entity
@Table(name = "accessibility_profiles")
public class AccessibilityProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "primary_disability")
    private DisabilityType primaryDisability = DisabilityType.NONE;

    @Column(name = "needs_sign_language_support")
    private Boolean needsSignLanguageSupport;

    @Column(name = "needs_subtitles")
    private Boolean needsSubtitles;

    @Column(name = "needs_audio_descriptions")
    private Boolean needsAudioDescriptions;

    @Column(name = "motor_impairment")
    private Boolean motorImpairment;

    @Column(name = "cognitive_support_required")
    private Boolean cognitiveSupportRequired;

    @Column(length = 1000)
    private String customNotes;

    public AccessibilityProfile() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public DisabilityType getPrimaryDisability() { return primaryDisability; }
    public void setPrimaryDisability(DisabilityType primaryDisability) { this.primaryDisability = primaryDisability; }

    public Boolean getNeedsSignLanguageSupport() { return needsSignLanguageSupport; }
    public void setNeedsSignLanguageSupport(Boolean needsSignLanguageSupport) { this.needsSignLanguageSupport = needsSignLanguageSupport; }

    public Boolean getNeedsSubtitles() { return needsSubtitles; }
    public void setNeedsSubtitles(Boolean needsSubtitles) { this.needsSubtitles = needsSubtitles; }

    public Boolean getNeedsAudioDescriptions() { return needsAudioDescriptions; }
    public void setNeedsAudioDescriptions(Boolean needsAudioDescriptions) { this.needsAudioDescriptions = needsAudioDescriptions; }

    public Boolean getMotorImpairment() { return motorImpairment; }
    public void setMotorImpairment(Boolean motorImpairment) { this.motorImpairment = motorImpairment; }

    public Boolean getCognitiveSupportRequired() { return cognitiveSupportRequired; }
    public void setCognitiveSupportRequired(Boolean cognitiveSupportRequired) { this.cognitiveSupportRequired = cognitiveSupportRequired; }

    public String getCustomNotes() { return customNotes; }
    public void setCustomNotes(String customNotes) { this.customNotes = customNotes; }
}
"@
Write-JavaFile -Path $accessibilityProfilePath -Content $accessibilityProfileContent

# 4.4 AuthenticationMethod
$authMethodPath = Join-Path $authModelDir "AuthenticationMethod.java"
$authMethodContent = @"
package com.inclusive.authservice.model;

import jakarta.persistence.*;

/**
 * Modelo para representar diferentes metodos de autenticacion por usuario.
 *
 * Archivo: auth-service/src/main/java/com/inclusive/authservice/model/AuthenticationMethod.java
 */
@Entity
@Table(name = "authentication_methods")
public class AuthenticationMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthMethodType type;

    @Column(name = "primary_method")
    private Boolean primaryMethod = Boolean.FALSE;

    @Column(name = "enabled")
    private Boolean enabled = Boolean.TRUE;

    @Column(name = "phone_number")
    private String phoneNumber;   // para OTP_SMS

    @Column(name = "external_id")
    private String externalId;    // para WebAuthn / OAuth / etc.

    public AuthenticationMethod() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public AuthMethodType getType() { return type; }
    public void setType(AuthMethodType type) { this.type = type; }

    public Boolean getPrimaryMethod() { return primaryMethod; }
    public void setPrimaryMethod(Boolean primaryMethod) { this.primaryMethod = primaryMethod; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getExternalId() { return externalId; }
    public void setExternalId(String externalId) { this.externalId = externalId; }
}
"@
Write-JavaFile -Path $authMethodPath -Content $authMethodContent

# ---------------------------------------------------------------------
# 5. adaptive-education-service: dominio Student completo y consistente
# ---------------------------------------------------------------------
Write-Host ""
Write-Host "5) Reconstruyendo dominio Student en adaptive-education-service..." -ForegroundColor Yellow

$adaptiveBase = "$root\adaptive-education-service\src\main\java\com\inclusive\adaptiveeducationservice"

# 5.1 Aplicacion principal
$adaptiveAppPath = Join-Path $adaptiveBase "AdaptiveEducationServiceApplication.java"
$adaptiveAppContent = @"
package com.inclusive.adaptiveeducationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Aplicacion principal del microservicio adaptive-education-service.
 *
 * Archivo: adaptive-education-service/src/main/java/com/inclusive/adaptiveeducationservice/AdaptiveEducationServiceApplication.java
 */
@SpringBootApplication
public class AdaptiveEducationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdaptiveEducationServiceApplication.class, args);
    }
}
"@
Write-JavaFile -Path $adaptiveAppPath -Content $adaptiveAppContent

# 5.2 Student entity
$studentEntityPath = Join-Path $adaptiveBase "entity\model\Student.java"
$studentEntityContent = @"
package com.inclusive.adaptiveeducationservice.entity.model;

import jakarta.persistence.*;

/**
 * Entidad Student pensada para:
 *  - almacenar informacion basica del estudiante
 *  - soporte a accesibilidad
 *  - generar datasets para ML/DL.
 *
 * Archivo: adaptive-education-service/src/main/java/com/inclusive/adaptiveeducationservice/entity/model/Student.java
 */
@Entity
@Table(name = "students")
public class Student {

    @Id
    private Long id; // puede corresponder al id de User en auth-service

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private Integer age;

    @Column
    private String grade;

    @Column
    private String institutionCode;

    // Estilos de aprendizaje / vocacional
    @Column
    private String kolbStyle;

    @Column
    private String felderSilvermanStyle;

    @Column
    private String careerInterestKuder;

    // Accesibilidad basica
    @Column
    private String disabilityType;  // VISUAL, HEARING, etc.

    @Column
    private Boolean highContrastEnabled;

    @Column
    private Boolean screenReaderEnabled;

    // Consentimiento para investigacion
    @Column
    private Boolean consentForResearch;

    public Student() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public String getInstitutionCode() { return institutionCode; }
    public void setInstitutionCode(String institutionCode) { this.institutionCode = institutionCode; }

    public String getKolbStyle() { return kolbStyle; }
    public void setKolbStyle(String kolbStyle) { this.kolbStyle = kolbStyle; }

    public String getFelderSilvermanStyle() { return felderSilvermanStyle; }
    public void setFelderSilvermanStyle(String felderSilvermanStyle) { this.felderSilvermanStyle = felderSilvermanStyle; }

    public String getCareerInterestKuder() { return careerInterestKuder; }
    public void setCareerInterestKuder(String careerInterestKuder) { this.careerInterestKuder = careerInterestKuder; }

    public String getDisabilityType() { return disabilityType; }
    public void setDisabilityType(String disabilityType) { this.disabilityType = disabilityType; }

    public Boolean getHighContrastEnabled() { return highContrastEnabled; }
    public void setHighContrastEnabled(Boolean highContrastEnabled) { this.highContrastEnabled = highContrastEnabled; }

    public Boolean getScreenReaderEnabled() { return screenReaderEnabled; }
    public void setScreenReaderEnabled(Boolean screenReaderEnabled) { this.screenReaderEnabled = screenReaderEnabled; }

    public Boolean getConsentForResearch() { return consentForResearch; }
    public void setConsentForResearch(Boolean consentForResearch) { this.consentForResearch = consentForResearch; }
}
"@
Write-JavaFile -Path $studentEntityPath -Content $studentEntityContent

# 5.3 StudentDTO (local del modulo, alineado con Student)
$studentDtoPath = Join-Path $adaptiveBase "entity\dto\StudentDTO.java"
$studentDtoContent = @"
package com.inclusive.adaptiveeducationservice.entity.dto;

/**
 * StudentDTO local de adaptive-education-service.
 *
 * Archivo: adaptive-education-service/src/main/java/com/inclusive/adaptiveeducationservice/entity/dto/StudentDTO.java
 */
public class StudentDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Integer age;

    private String grade;
    private String institutionCode;

    private String kolbStyle;
    private String felderSilvermanStyle;
    private String careerInterestKuder;

    private String disabilityType;
    private Boolean highContrastEnabled;
    private Boolean screenReaderEnabled;
    private Boolean consentForResearch;

    public StudentDTO() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public String getInstitutionCode() { return institutionCode; }
    public void setInstitutionCode(String institutionCode) { this.institutionCode = institutionCode; }

    public String getKolbStyle() { return kolbStyle; }
    public void setKolbStyle(String kolbStyle) { this.kolbStyle = kolbStyle; }

    public String getFelderSilvermanStyle() { return felderSilvermanStyle; }
    public void setFelderSilvermanStyle(String felderSilvermanStyle) { this.felderSilvermanStyle = felderSilvermanStyle; }

    public String getCareerInterestKuder() { return careerInterestKuder; }
    public void setCareerInterestKuder(String careerInterestKuder) { this.careerInterestKuder = careerInterestKuder; }

    public String getDisabilityType() { return disabilityType; }
    public void setDisabilityType(String disabilityType) { this.disabilityType = disabilityType; }

    public Boolean getHighContrastEnabled() { return highContrastEnabled; }
    public void setHighContrastEnabled(Boolean highContrastEnabled) { this.highContrastEnabled = highContrastEnabled; }

    public Boolean getScreenReaderEnabled() { return screenReaderEnabled; }
    public void setScreenReaderEnabled(Boolean screenReaderEnabled) { this.screenReaderEnabled = screenReaderEnabled; }

    public Boolean getConsentForResearch() { return consentForResearch; }
    public void setConsentForResearch(Boolean consentForResearch) { this.consentForResearch = consentForResearch; }
}
"@
Write-JavaFile -Path $studentDtoPath -Content $studentDtoContent

# 5.4 StudentMapper
$studentMapperPath = Join-Path $adaptiveBase "entity\model\StudentMapper.java"
$studentMapperContent = @"
package com.inclusive.adaptiveeducationservice.entity.model;

import com.inclusive.adaptiveeducationservice.entity.dto.StudentDTO;

/**
 * Mapper sencillo entre Student y StudentDTO.
 *
 * Archivo: adaptive-education-service/src/main/java/com/inclusive/adaptiveeducationservice/entity/model/StudentMapper.java
 */
public class StudentMapper {

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
        dto.setInstitutionCode(student.getInstitutionCode());
        dto.setKolbStyle(student.getKolbStyle());
        dto.setFelderSilvermanStyle(student.getFelderSilvermanStyle());
        dto.setCareerInterestKuder(student.getCareerInterestKuder());
        dto.setDisabilityType(student.getDisabilityType());
        dto.setHighContrastEnabled(student.getHighContrastEnabled());
        dto.setScreenReaderEnabled(student.getScreenReaderEnabled());
        dto.setConsentForResearch(student.getConsentForResearch());
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
        student.setInstitutionCode(dto.getInstitutionCode());
        student.setKolbStyle(dto.getKolbStyle());
        student.setFelderSilvermanStyle(dto.getFelderSilvermanStyle());
        student.setCareerInterestKuder(dto.getCareerInterestKuder());
        student.setDisabilityType(dto.getDisabilityType());
        student.setHighContrastEnabled(dto.getHighContrastEnabled());
        student.setScreenReaderEnabled(dto.getScreenReaderEnabled());
        student.setConsentForResearch(dto.getConsentForResearch());
        return student;
    }
}
"@
Write-JavaFile -Path $studentMapperPath -Content $studentMapperContent

# 5.5 StudentRepository
$studentRepoPath = Join-Path $adaptiveBase "entity\repository\StudentRepository.java"
$studentRepoContent = @"
package com.inclusive.adaptiveeducationservice.entity.repository;

import com.inclusive.adaptiveeducationservice.entity.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio JPA para Student.
 *
 * Archivo: adaptive-education-service/src/main/java/com/inclusive/adaptiveeducationservice/entity/repository/StudentRepository.java
 */
public interface StudentRepository extends JpaRepository<Student, Long> {

    boolean existsByEmail(String email);

    Optional<Student> findByEmail(String email);
}
"@
Write-JavaFile -Path $studentRepoPath -Content $studentRepoContent

# 5.6 StudentService
$studentServicePath = Join-Path $adaptiveBase "entity\service\StudentService.java"
$studentServiceContent = @"
package com.inclusive.adaptiveeducationservice.entity.service;

import com.inclusive.adaptiveeducationservice.entity.dto.StudentDTO;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de dominio para manejar estudiantes y exponer operaciones
 * listas para un MVP completo y datasets de IA.
 *
 * Archivo: adaptive-education-service/src/main/java/com/inclusive/adaptiveeducationservice/entity/service/StudentService.java
 */
public interface StudentService {

    List<StudentDTO> findAll();

    Optional<StudentDTO> findById(Long id);

    Optional<StudentDTO> findByEmail(String email);

    StudentDTO create(StudentDTO dto);

    StudentDTO update(Long id, StudentDTO dto);

    void delete(Long id);
}
"@
Write-JavaFile -Path $studentServicePath -Content $studentServiceContent

# 5.7 StudentServiceImpl
$studentServiceImplPath = Join-Path $adaptiveBase "entity\service\impl\StudentServiceImpl.java"
$studentServiceImplContent = @"
package com.inclusive.adaptiveeducationservice.entity.service.impl;

import com.inclusive.adaptiveeducationservice.entity.dto.StudentDTO;
import com.inclusive.adaptiveeducationservice.entity.model.Student;
import com.inclusive.adaptiveeducationservice.entity.model.StudentMapper;
import com.inclusive.adaptiveeducationservice.entity.repository.StudentRepository;
import com.inclusive.adaptiveeducationservice.entity.service.StudentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementacion de StudentService.
 *
 * Archivo: adaptive-education-service/src/main/java/com/inclusive/adaptiveeducationservice/entity/service/impl/StudentServiceImpl.java
 */
@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository repository;

    public StudentServiceImpl(StudentRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<StudentDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(StudentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<StudentDTO> findById(Long id) {
        return repository.findById(id).map(StudentMapper::toDto);
    }

    @Override
    public Optional<StudentDTO> findByEmail(String email) {
        return repository.findByEmail(email).map(StudentMapper::toDto);
    }

    @Override
    public StudentDTO create(StudentDTO dto) {
        if (dto.getEmail() != null && repository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("A student with the same email already exists.");
        }
        Student student = StudentMapper.toEntity(dto);
        Student saved = repository.save(student);
        return StudentMapper.toDto(saved);
    }

    @Override
    public StudentDTO update(Long id, StudentDTO dto) {
        Student existing = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with id " + id));

        // Actualizacion campo a campo (manteniendo el dataset rico)
        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setEmail(dto.getEmail());
        existing.setAge(dto.getAge());
        existing.setGrade(dto.getGrade());
        existing.setInstitutionCode(dto.getInstitutionCode());
        existing.setKolbStyle(dto.getKolbStyle());
        existing.setFelderSilvermanStyle(dto.getFelderSilvermanStyle());
        existing.setCareerInterestKuder(dto.getCareerInterestKuder());
        existing.setDisabilityType(dto.getDisabilityType());
        existing.setHighContrastEnabled(dto.getHighContrastEnabled());
        existing.setScreenReaderEnabled(dto.getScreenReaderEnabled());
        existing.setConsentForResearch(dto.getConsentForResearch());

        Student saved = repository.save(existing);
        return StudentMapper.toDto(saved);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
"@
Write-JavaFile -Path $studentServiceImplPath -Content $studentServiceImplContent

# 5.8 DataInitializer
$dataInitializerPath = Join-Path $adaptiveBase "config\DataInitializer.java"
$dataInitializerContent = @"
package com.inclusive.adaptiveeducationservice.config;

import com.inclusive.adaptiveeducationservice.entity.model.Student;
import com.inclusive.adaptiveeducationservice.entity.repository.StudentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Inicializador de datos para generar estudiantes de ejemplo
 * en la base de datos (util para pruebas y datasets iniciales).
 *
 * Archivo: adaptive-education-service/src/main/java/com/inclusive/adaptiveeducationservice/config/DataInitializer.java
 */
@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(StudentRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                for (int i = 1; i <= 10; i++) {
                    Student s = new Student();
                    s.setId((long) i);
                    s.setFirstName("Student" + i);
                    s.setLastName("Demo");
                    s.setEmail("student" + i + "@example.com");
                    s.setAge(10 + i);
                    s.setGrade("Grade " + (i % 5 + 1));
                    s.setInstitutionCode("INST-001");
                    s.setKolbStyle("DIVERGING");
                    s.setFelderSilvermanStyle("ACTIVE_REFLECTIVE");
                    s.setCareerInterestKuder("SCIENCE");
                    s.setDisabilityType("NONE");
                    s.setHighContrastEnabled(Boolean.FALSE);
                    s.setScreenReaderEnabled(Boolean.FALSE);
                    s.setConsentForResearch(Boolean.TRUE);
                    repository.save(s);
                }
                System.out.println("Created 10 demo students in PostgreSQL.");
            } else {
                System.out.println("Students already exist in the database; no demo data created.");
            }
        };
    }
}
"@
Write-JavaFile -Path $dataInitializerPath -Content $dataInitializerContent

# 5.9 StudentProxyController (API REST del modulo)
$studentProxyControllerPath = Join-Path $adaptiveBase "controller\StudentProxyController.java"
$studentProxyControllerContent = @"
package com.inclusive.adaptiveeducationservice.controller;

import com.inclusive.adaptiveeducationservice.entity.dto.StudentDTO;
import com.inclusive.adaptiveeducationservice.entity.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST principal para exponer operaciones sobre estudiantes.
 *
 * Archivo: adaptive-education-service/src/main/java/com/inclusive/adaptiveeducationservice/controller/StudentProxyController.java
 */
@RestController
@RequestMapping("/api/adaptive/students")
public class StudentProxyController {

    private final StudentService studentService;

    public StudentProxyController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public ResponseEntity<List<StudentDTO>> getAll() {
        return ResponseEntity.ok(studentService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentDTO> getById(@PathVariable Long id) {
        return studentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<StudentDTO> create(@RequestBody StudentDTO dto) {
        return ResponseEntity.ok(studentService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentDTO> update(@PathVariable Long id, @RequestBody StudentDTO dto) {
        return ResponseEntity.ok(studentService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        studentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
"@
Write-JavaFile -Path $studentProxyControllerPath -Content $studentProxyControllerContent

# (Opcional) 5.10 Stubs de StudentClient y StudentIntegrationService
# Para que el modulo compile aunque luego queramos expandir integraciones.
$studentClientPath = Join-Path $adaptiveBase "client\StudentClient.java"
$studentClientContent = @"
package com.inclusive.adaptiveeducationservice.client;

import com.inclusive.adaptiveeducationservice.entity.dto.StudentDTO;

import java.util.Optional;

/**
 * Stub de cliente para integraciones futuras.
 * De momento, no realiza llamadas remotas, solo define la interfaz.
 *
 * Archivo: adaptive-education-service/src/main/java/com/inclusive/adaptiveeducationservice/client/StudentClient.java
 */
public class StudentClient {

    public Optional<StudentDTO> getStudentById(Long id) {
        // En un futuro: llamada a otro microservicio o gateway.
        return Optional.empty();
    }
}
"@
Write-JavaFile -Path $studentClientPath -Content $studentClientContent

$studentIntegrationServicePath = Join-Path $adaptiveBase "service\StudentIntegrationService.java"
$studentIntegrationServiceContent = @"
package com.inclusive.adaptiveeducationservice.service;

import com.inclusive.adaptiveeducationservice.entity.dto.StudentDTO;

import java.util.Optional;

/**
 * Servicio de integracion. Por ahora actua como capa de abstraccion
 * y se puede conectar a StudentService o a StudentClient segun convenga.
 *
 * Archivo: adaptive-education-service/src/main/java/com/inclusive/adaptiveeducationservice/service/StudentIntegrationService.java
 */
public interface StudentIntegrationService {

    Optional<StudentDTO> fetchStudentById(Long id);
}
"@
Write-JavaFile -Path $studentIntegrationServicePath -Content $studentIntegrationServiceContent

$studentIntegrationServiceImplPath = Join-Path $adaptiveBase "service\impl\StudentIntegrationServiceImpl.java"
$studentIntegrationServiceImplContent = @"
package com.inclusive.adaptiveeducationservice.service.impl;

import com.inclusive.adaptiveeducationservice.entity.dto.StudentDTO;
import com.inclusive.adaptiveeducationservice.entity.service.StudentService;
import com.inclusive.adaptiveeducationservice.service.StudentIntegrationService;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementacion basica de StudentIntegrationService apoyada en StudentService.
 *
 * Archivo: adaptive-education-service/src/main/java/com/inclusive/adaptiveeducationservice/service/impl/StudentIntegrationServiceImpl.java
 */
@Service
public class StudentIntegrationServiceImpl implements StudentIntegrationService {

    private final StudentService studentService;

    public StudentIntegrationServiceImpl(StudentService studentService) {
        this.studentService = studentService;
    }

    @Override
    public Optional<StudentDTO> fetchStudentById(Long id) {
        return studentService.findById(id);
    }
}
"@
Write-JavaFile -Path $studentIntegrationServiceImplPath -Content $studentIntegrationServiceImplContent

# ---------------------------------------------------------------------
# 6. Mensaje final y sugerencia de build
# ---------------------------------------------------------------------
Write-Host ""
Write-Host "===================================================" -ForegroundColor Cyan
Write-Host " Reconstruccion de modulos completada."             -ForegroundColor Cyan
Write-Host " Ahora puedes probar el build con:"                  -ForegroundColor Cyan
Write-Host "   mvn clean package -DskipTests"                    -ForegroundColor Cyan
Write-Host " (o solo el modulo: -pl adaptive-education-service)" -ForegroundColor Cyan
Write-Host "===================================================" -ForegroundColor Cyan
