param()

$ErrorActionPreference = "Stop"

Write-Host "==============================================="
Write-Host " ILP MEGA-SCRIPT FULL BACKEND (REPAIR + CRUD)"
Write-Host "==============================================="
$projectRoot = Get-Location
Write-Host "ProjectRoot: $projectRoot"

function Ensure-Dir {
    param([string]$Path)
    if (-not (Test-Path $Path)) {
        New-Item -ItemType Directory -Path $Path | Out-Null
        Write-Host "[DIR] Created: $Path"
    } else {
        Write-Host "[DIR] Exists:  $Path"
    }
}

function Write-FileIfMissing {
    param(
        [string]$Path,
        [string]$Content
    )
    if (Test-Path $Path) {
        Write-Host "[SKIP] File already exists, not overwriting: $Path"
    } else {
        $dir = Split-Path $Path -Parent
        if (-not (Test-Path $dir)) {
            New-Item -ItemType Directory -Path $dir | Out-Null
        }
        Set-Content -Path $Path -Value $Content -Encoding UTF8
        Write-Host "[OK]   Wrote: $Path"
    }
}

# ==========================================================
# 1. Validar módulos existentes
# ==========================================================
$modules = @(
    "auth-service",
    "tenant-service",
    "assessment-service",
    "notification-service",
    "monitoring-service",
    "report-service"
)

foreach ($m in $modules) {
    $path = Join-Path $projectRoot $m
    if (-not (Test-Path $path)) {
        Write-Host "[WARN] Module not found, skipping: $m at $path" -ForegroundColor Yellow
    } else {
        Write-Host "[INFO] Module OK: $m -> $path"
    }
}

# ==========================================================
# 2. AUTH-SERVICE (UserAccount CRUD)
# ==========================================================
$authModulePath = Join-Path $projectRoot "auth-service"
if (Test-Path $authModulePath) {
    $authBasePackage = "com.inclusive.authservice"
    $authBasePath = Join-Path $authModulePath "src/main/java/com/inclusive/authservice"

    Ensure-Dir $authBasePath

    # Entity
    $authEntityPath = Join-Path $authBasePath "entity/UserAccount.java"
    $authEntityContent = @"
// File: auth-service/src/main/java/com/inclusive/authservice/entity/UserAccount.java
package $authBasePackage.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user_accounts")
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 120)
    private String username;

    @Column(nullable = false, unique = true, length = 180)
    private String email;

    @Column(nullable = false, length = 255)
    private String passwordHash;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_account_roles", joinColumns = @JoinColumn(name = "user_account_id"))
    @Column(name = "role")
    private Set<String> roles = new HashSet<>();

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false)
    private String tenantKey;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    public UserAccount() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public String getTenantKey() { return tenantKey; }
    public void setTenantKey(String tenantKey) { this.tenantKey = tenantKey; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
"@
    Write-FileIfMissing -Path $authEntityPath -Content $authEntityContent

    # DTO
    $authDtoPath = Join-Path $authBasePath "dto/UserAccountDTO.java"
    $authDtoContent = @"
// File: auth-service/src/main/java/com/inclusive/authservice/dto/UserAccountDTO.java
package $authBasePackage.dto;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Set;

public class UserAccountDTO implements Serializable {

    private Long id;
    private String username;
    private String email;
    private Set<String> roles;
    private Boolean active;
    private String tenantKey;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public UserAccountDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public String getTenantKey() { return tenantKey; }
    public void setTenantKey(String tenantKey) { this.tenantKey = tenantKey; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
"@
    Write-FileIfMissing -Path $authDtoPath -Content $authDtoContent

    # Mapper
    $authMapperPath = Join-Path $authBasePath "mapper/UserAccountMapper.java"
    $authMapperContent = @"
// File: auth-service/src/main/java/com/inclusive/authservice/mapper/UserAccountMapper.java
package $authBasePackage.mapper;

import $authBasePackage.entity.UserAccount;
import $authBasePackage.dto.UserAccountDTO;

public class UserAccountMapper {

    private UserAccountMapper() {}

    public static UserAccountDTO toDto(UserAccount entity) {
        if (entity == null) return null;
        UserAccountDTO dto = new UserAccountDTO();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setEmail(entity.getEmail());
        dto.setRoles(entity.getRoles());
        dto.setActive(entity.getActive());
        dto.setTenantKey(entity.getTenantKey());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    public static UserAccount toEntity(UserAccountDTO dto) {
        if (dto == null) return null;
        UserAccount entity = new UserAccount();
        entity.setId(dto.getId());
        entity.setUsername(dto.getUsername());
        entity.setEmail(dto.getEmail());
        entity.setRoles(dto.getRoles());
        entity.setActive(dto.getActive());
        entity.setTenantKey(dto.getTenantKey());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());
        return entity;
    }
}
"@
    Write-FileIfMissing -Path $authMapperPath -Content $authMapperContent

    # Repository
    $authRepoPath = Join-Path $authBasePath "repository/UserAccountRepository.java"
    $authRepoContent = @"
// File: auth-service/src/main/java/com/inclusive/authservice/repository/UserAccountRepository.java
package $authBasePackage.repository;

import $authBasePackage.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    Optional<UserAccount> findByUsernameAndTenantKey(String username, String tenantKey);

    Optional<UserAccount> findByEmailAndTenantKey(String email, String tenantKey);
}
"@
    Write-FileIfMissing -Path $authRepoPath -Content $authRepoContent

    # Service
    $authServicePath = Join-Path $authBasePath "service/UserAccountService.java"
    $authServiceContent = @"
// File: auth-service/src/main/java/com/inclusive/authservice/service/UserAccountService.java
package $authBasePackage.service;

import $authBasePackage.dto.UserAccountDTO;

import java.util.List;

public interface UserAccountService {

    UserAccountDTO create(UserAccountDTO dto);

    UserAccountDTO update(Long id, UserAccountDTO dto);

    void delete(Long id);

    UserAccountDTO findById(Long id);

    List<UserAccountDTO> findAllByTenant(String tenantKey);
}
"@
    Write-FileIfMissing -Path $authServicePath -Content $authServiceContent

    # ServiceImpl
    $authServiceImplPath = Join-Path $authBasePath "service/impl/UserAccountServiceImpl.java"
    $authServiceImplContent = @"
// File: auth-service/src/main/java/com/inclusive/authservice/service/impl/UserAccountServiceImpl.java
package $authBasePackage.service.impl;

import $authBasePackage.dto.UserAccountDTO;
import $authBasePackage.entity.UserAccount;
import $authBasePackage.mapper.UserAccountMapper;
import $authBasePackage.repository.UserAccountRepository;
import $authBasePackage.service.UserAccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserAccountServiceImpl implements UserAccountService {

    private final UserAccountRepository repository;

    public UserAccountServiceImpl(UserAccountRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserAccountDTO create(UserAccountDTO dto) {
        UserAccount entity = UserAccountMapper.toEntity(dto);
        UserAccount saved = repository.save(entity);
        return UserAccountMapper.toDto(saved);
    }

    @Override
    public UserAccountDTO update(Long id, UserAccountDTO dto) {
        UserAccount existing = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("UserAccount not found with id " + id));
        existing.setUsername(dto.getUsername());
        existing.setEmail(dto.getEmail());
        existing.setRoles(dto.getRoles());
        existing.setActive(dto.getActive());
        existing.setTenantKey(dto.getTenantKey());
        UserAccount saved = repository.save(existing);
        return UserAccountMapper.toDto(saved);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public UserAccountDTO findById(Long id) {
        return repository.findById(id)
                .map(UserAccountMapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("UserAccount not found with id " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserAccountDTO> findAllByTenant(String tenantKey) {
        return repository.findAll().stream()
                .filter(u -> tenantKey.equals(u.getTenantKey()))
                .map(UserAccountMapper::toDto)
                .collect(Collectors.toList());
    }
}
"@
    Write-FileIfMissing -Path $authServiceImplPath -Content $authServiceImplContent

    # Controller
    $authControllerPath = Join-Path $authBasePath "controller/UserAccountController.java"
    $authControllerContent = @"
// File: auth-service/src/main/java/com/inclusive/authservice/controller/UserAccountController.java
package $authBasePackage.controller;

import $authBasePackage.dto.UserAccountDTO;
import $authBasePackage.service.UserAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(\"/api/auth/users\")
public class UserAccountController {

    private final UserAccountService service;

    public UserAccountController(UserAccountService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<UserAccountDTO> create(
            @RequestHeader(\"X-Tenant-Key\") String tenantKey,
            @RequestBody UserAccountDTO dto) {
        dto.setTenantKey(tenantKey);
        UserAccountDTO created = service.create(dto);
        return ResponseEntity
                .created(URI.create(\"/api/auth/users/\" + created.getId()))
                .body(created);
    }

    @PutMapping(\"/{id}\")
    public ResponseEntity<UserAccountDTO> update(
            @PathVariable Long id,
            @RequestBody UserAccountDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @GetMapping(\"/{id}\")
    public ResponseEntity<UserAccountDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<UserAccountDTO>> list(
            @RequestHeader(\"X-Tenant-Key\") String tenantKey) {
        return ResponseEntity.ok(service.findAllByTenant(tenantKey));
    }

    @DeleteMapping(\"/{id}\")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(\"/health\")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok(\"AUTH-SERVICE OK\");
    }
}
"@
    Write-FileIfMissing -Path $authControllerPath -Content $authControllerContent
}

# ==========================================================
# 3. TENANT-SERVICE (Tenant CRUD)
# ==========================================================
$tenantModulePath = Join-Path $projectRoot "tenant-service"
if (Test-Path $tenantModulePath) {
    $tenantBasePackage = "com.inclusive.tenantservice"
    $tenantBasePath = Join-Path $tenantModulePath "src/main/java/com/inclusive/tenantservice"
    Ensure-Dir $tenantBasePath

    $tenantEntityPath = Join-Path $tenantBasePath "entity/Tenant.java"
    $tenantEntityContent = @"
// File: tenant-service/src/main/java/com/inclusive/tenantservice/entity/Tenant.java
package $tenantBasePackage.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "tenants")
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String tenantKey;

    @Column(nullable = false, length = 180)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(nullable = false)
    private Boolean active = true;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    public Tenant() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTenantKey() { return tenantKey; }
    public void setTenantKey(String tenantKey) { this.tenantKey = tenantKey; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
"@
    Write-FileIfMissing -Path $tenantEntityPath -Content $tenantEntityContent

    $tenantDtoPath = Join-Path $tenantBasePath "dto/TenantDTO.java"
    $tenantDtoContent = @"
// File: tenant-service/src/main/java/com/inclusive/tenantservice/dto/TenantDTO.java
package $tenantBasePackage.dto;

import java.io.Serializable;
import java.time.OffsetDateTime;

public class TenantDTO implements Serializable {

    private Long id;
    private String tenantKey;
    private String name;
    private String description;
    private Boolean active;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public TenantDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTenantKey() { return tenantKey; }
    public void setTenantKey(String tenantKey) { this.tenantKey = tenantKey; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
"@
    Write-FileIfMissing -Path $tenantDtoPath -Content $tenantDtoContent

    $tenantMapperPath = Join-Path $tenantBasePath "mapper/TenantMapper.java"
    $tenantMapperContent = @"
// File: tenant-service/src/main/java/com/inclusive/tenantservice/mapper/TenantMapper.java
package $tenantBasePackage.mapper;

import $tenantBasePackage.entity.Tenant;
import $tenantBasePackage.dto.TenantDTO;

public class TenantMapper {

    private TenantMapper() {}

    public static TenantDTO toDto(Tenant entity) {
        if (entity == null) return null;
        TenantDTO dto = new TenantDTO();
        dto.setId(entity.getId());
        dto.setTenantKey(entity.getTenantKey());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setActive(entity.getActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    public static Tenant toEntity(TenantDTO dto) {
        if (dto == null) return null;
        Tenant entity = new Tenant();
        entity.setId(dto.getId());
        entity.setTenantKey(dto.getTenantKey());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setActive(dto.getActive());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());
        return entity;
    }
}
"@
    Write-FileIfMissing -Path $tenantMapperPath -Content $tenantMapperContent

    $tenantRepoPath = Join-Path $tenantBasePath "repository/TenantRepository.java"
    $tenantRepoContent = @"
// File: tenant-service/src/main/java/com/inclusive/tenantservice/repository/TenantRepository.java
package $tenantBasePackage.repository;

import $tenantBasePackage.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TenantRepository extends JpaRepository<Tenant, Long> {

    Optional<Tenant> findByTenantKey(String tenantKey);
}
"@
    Write-FileIfMissing -Path $tenantRepoPath -Content $tenantRepoContent

    $tenantServicePath = Join-Path $tenantBasePath "service/TenantService.java"
    $tenantServiceContent = @"
// File: tenant-service/src/main/java/com/inclusive/tenantservice/service/TenantService.java
package $tenantBasePackage.service;

import $tenantBasePackage.dto.TenantDTO;

import java.util.List;

public interface TenantService {

    TenantDTO create(TenantDTO dto);

    TenantDTO update(Long id, TenantDTO dto);

    void delete(Long id);

    TenantDTO findById(Long id);

    TenantDTO findByTenantKey(String tenantKey);

    List<TenantDTO> findAll();
}
"@
    Write-FileIfMissing -Path $tenantServicePath -Content $tenantServiceContent

    $tenantServiceImplPath = Join-Path $tenantBasePath "service/impl/TenantServiceImpl.java"
    $tenantServiceImplContent = @"
// File: tenant-service/src/main/java/com/inclusive/tenantservice/service/impl/TenantServiceImpl.java
package $tenantBasePackage.service.impl;

import $tenantBasePackage.dto.TenantDTO;
import $tenantBasePackage.entity.Tenant;
import $tenantBasePackage.mapper.TenantMapper;
import $tenantBasePackage.repository.TenantRepository;
import $tenantBasePackage.service.TenantService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TenantServiceImpl implements TenantService {

    private final TenantRepository repository;

    public TenantServiceImpl(TenantRepository repository) {
        this.repository = repository;
    }

    @Override
    public TenantDTO create(TenantDTO dto) {
        Tenant entity = TenantMapper.toEntity(dto);
        Tenant saved = repository.save(entity);
        return TenantMapper.toDto(saved);
    }

    @Override
    public TenantDTO update(Long id, TenantDTO dto) {
        Tenant existing = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found with id " + id));
        existing.setTenantKey(dto.getTenantKey());
        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setActive(dto.getActive());
        Tenant saved = repository.save(existing);
        return TenantMapper.toDto(saved);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public TenantDTO findById(Long id) {
        return repository.findById(id)
                .map(TenantMapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found with id " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public TenantDTO findByTenantKey(String tenantKey) {
        return repository.findByTenantKey(tenantKey)
                .map(TenantMapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Tenant not found with key " + tenantKey));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TenantDTO> findAll() {
        return repository.findAll().stream()
                .map(TenantMapper::toDto)
                .collect(Collectors.toList());
    }
}
"@
    Write-FileIfMissing -Path $tenantServiceImplPath -Content $tenantServiceImplContent

    $tenantControllerPath = Join-Path $tenantBasePath "controller/TenantController.java"
    $tenantControllerContent = @"
// File: tenant-service/src/main/java/com/inclusive/tenantservice/controller/TenantController.java
package $tenantBasePackage.controller;

import $tenantBasePackage.dto.TenantDTO;
import $tenantBasePackage.service.TenantService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(\"/api/tenants\")
public class TenantController {

    private final TenantService service;

    public TenantController(TenantService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TenantDTO> create(@RequestBody TenantDTO dto) {
        TenantDTO created = service.create(dto);
        return ResponseEntity
                .created(URI.create(\"/api/tenants/\" + created.getId()))
                .body(created);
    }

    @PutMapping(\"/{id}\")
    public ResponseEntity<TenantDTO> update(@PathVariable Long id, @RequestBody TenantDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @GetMapping(\"/{id}\")
    public ResponseEntity<TenantDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping(\"/by-key/{tenantKey}\")
    public ResponseEntity<TenantDTO> getByTenantKey(@PathVariable String tenantKey) {
        return ResponseEntity.ok(service.findByTenantKey(tenantKey));
    }

    @GetMapping
    public ResponseEntity<List<TenantDTO>> list() {
        return ResponseEntity.ok(service.findAll());
    }

    @DeleteMapping(\"/{id}\")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(\"/health\")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok(\"TENANT-SERVICE OK\");
    }
}
"@
    Write-FileIfMissing -Path $tenantControllerPath -Content $tenantControllerContent
}

# ==========================================================
# 4. Helper gen para otros módulos (Assessment, Notification, Monitoring, Report)
# ==========================================================
function New-GenericCrudModule {
    param(
        [string]$ModuleName,
        [string]$BasePackage,
        [string]$EntityName,
        [string]$ApiBasePath
    )

    $modulePath = Join-Path $projectRoot $ModuleName
    if (-not (Test-Path $modulePath)) {
        Write-Host "[WARN] Skipping missing module: $ModuleName"
        return
    }

    $basePath = Join-Path $modulePath ("src/main/java/" + $BasePackage.Replace('.', '/'))
    Ensure-Dir $basePath

    $entityPath = Join-Path $basePath "entity/$EntityName.java"
    $dtoPath = Join-Path $basePath "dto/${EntityName}DTO.java"
    $mapperPath = Join-Path $basePath "mapper/${EntityName}Mapper.java"
    $repoPath = Join-Path $basePath "repository/${EntityName}Repository.java"
    $servicePath = Join-Path $basePath "service/${EntityName}Service.java"
    $serviceImplPath = Join-Path $basePath "service/impl/${EntityName}ServiceImpl.java"
    $controllerPath = Join-Path $basePath "controller/${EntityName}Controller.java"

    $entityContent = @"
// File: $ModuleName/src/main/java/$($BasePackage.Replace('.','/'))/entity/$EntityName.java
package $BasePackage.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = \""$($EntityName.ToLower())s\"")
public class $EntityName {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 180)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(nullable = false)
    private Boolean active = true;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    public $EntityName() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
"@
    Write-FileIfMissing -Path $entityPath -Content $entityContent

    $dtoContent = @"
// File: $ModuleName/src/main/java/$($BasePackage.Replace('.','/'))/dto/${EntityName}DTO.java
package $BasePackage.dto;

import java.io.Serializable;
import java.time.OffsetDateTime;

public class ${EntityName}DTO implements Serializable {

    private Long id;
    private String name;
    private String description;
    private Boolean active;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public ${EntityName}DTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
"@
    Write-FileIfMissing -Path $dtoPath -Content $dtoContent

    $mapperContent = @"
// File: $ModuleName/src/main/java/$($BasePackage.Replace('.','/'))/mapper/${EntityName}Mapper.java
package $BasePackage.mapper;

import $BasePackage.entity.$EntityName;
import $BasePackage.dto.${EntityName}DTO;

public class ${EntityName}Mapper {

    private ${EntityName}Mapper() {}

    public static ${EntityName}DTO toDto($EntityName entity) {
        if (entity == null) return null;
        ${EntityName}DTO dto = new ${EntityName}DTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setActive(entity.getActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    public static $EntityName toEntity(${EntityName}DTO dto) {
        if (dto == null) return null;
        $EntityName entity = new $EntityName();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setActive(dto.getActive());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());
        return entity;
    }
}
"@
    Write-FileIfMissing -Path $mapperPath -Content $mapperContent

    $repoContent = @"
// File: $ModuleName/src/main/java/$($BasePackage.Replace('.','/'))/repository/${EntityName}Repository.java
package $BasePackage.repository;

import $BasePackage.entity.$EntityName;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ${EntityName}Repository extends JpaRepository<$EntityName, Long> {
}
"@
    Write-FileIfMissing -Path $repoPath -Content $repoContent

    $serviceContent = @"
// File: $ModuleName/src/main/java/$($BasePackage.Replace('.','/'))/service/${EntityName}Service.java
package $BasePackage.service;

import $BasePackage.dto.${EntityName}DTO;

import java.util.List;

public interface ${EntityName}Service {

    ${EntityName}DTO create(${EntityName}DTO dto);

    ${EntityName}DTO update(Long id, ${EntityName}DTO dto);

    void delete(Long id);

    ${EntityName}DTO findById(Long id);

    List<${EntityName}DTO> findAll();
}
"@
    Write-FileIfMissing -Path $servicePath -Content $serviceContent

    $serviceImplContent = @"
// File: $ModuleName/src/main/java/$($BasePackage.Replace('.','/'))/service/impl/${EntityName}ServiceImpl.java
package $BasePackage.service.impl;

import $BasePackage.dto.${EntityName}DTO;
import $BasePackage.entity.$EntityName;
import $BasePackage.mapper.${EntityName}Mapper;
import $BasePackage.repository.${EntityName}Repository;
import $BasePackage.service.${EntityName}Service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ${EntityName}ServiceImpl implements ${EntityName}Service {

    private final ${EntityName}Repository repository;

    public ${EntityName}ServiceImpl(${EntityName}Repository repository) {
        this.repository = repository;
    }

    @Override
    public ${EntityName}DTO create(${EntityName}DTO dto) {
        $EntityName entity = ${EntityName}Mapper.toEntity(dto);
        $EntityName saved = repository.save(entity);
        return ${EntityName}Mapper.toDto(saved);
    }

    @Override
    public ${EntityName}DTO update(Long id, ${EntityName}DTO dto) {
        $EntityName existing = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(\"$EntityName not found with id \" + id));
        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setActive(dto.getActive());
        $EntityName saved = repository.save(existing);
        return ${EntityName}Mapper.toDto(saved);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ${EntityName}DTO findById(Long id) {
        return repository.findById(id)
                .map(${EntityName}Mapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException(\"$EntityName not found with id \" + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<${EntityName}DTO> findAll() {
        return repository.findAll().stream()
                .map(${EntityName}Mapper::toDto)
                .collect(Collectors.toList());
    }
}
"@
    Write-FileIfMissing -Path $serviceImplPath -Content $serviceImplContent

    $controllerContent = @"
// File: $ModuleName/src/main/java/$($BasePackage.Replace('.','/'))/controller/${EntityName}Controller.java
package $BasePackage.controller;

import $BasePackage.dto.${EntityName}DTO;
import $BasePackage.service.${EntityName}Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(\"$ApiBasePath\")
public class ${EntityName}Controller {

    private final ${EntityName}Service service;

    public ${EntityName}Controller(${EntityName}Service service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<${EntityName}DTO> create(@RequestBody ${EntityName}DTO dto) {
        ${EntityName}DTO created = service.create(dto);
        return ResponseEntity
                .created(URI.create(\"$ApiBasePath/\" + created.getId()))
                .body(created);
    }

    @PutMapping(\"/{id}\")
    public ResponseEntity<${EntityName}DTO> update(@PathVariable Long id, @RequestBody ${EntityName}DTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @GetMapping(\"/{id}\")
    public ResponseEntity<${EntityName}DTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<${EntityName}DTO>> list() {
        return ResponseEntity.ok(service.findAll());
    }

    @DeleteMapping(\"/{id}\")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(\"/health\")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok(\"$ModuleName OK\");
    }
}
"@
    Write-FileIfMissing -Path $controllerPath -Content $controllerContent
}

# Assessment-service: AssessmentTemplate
New-GenericCrudModule -ModuleName "assessment-service" `
    -BasePackage "com.inclusive.assessmentservice" `
    -EntityName "AssessmentTemplate" `
    -ApiBasePath "/api/assessments/templates"

# Notification-service: Notification
New-GenericCrudModule -ModuleName "notification-service" `
    -BasePackage "com.inclusive.notificationservice" `
    -EntityName "Notification" `
    -ApiBasePath "/api/notifications"

# Monitoring-service: MonitoringEvent
New-GenericCrudModule -ModuleName "monitoring-service" `
    -BasePackage "com.inclusive.monitoringservice" `
    -EntityName "MonitoringEvent" `
    -ApiBasePath "/api/monitoring/events"

# Report-service: ReportJob
New-GenericCrudModule -ModuleName "report-service" `
    -BasePackage "com.inclusive.reportservice" `
    -EntityName "ReportJob" `
    -ApiBasePath "/api/reports/jobs"

Write-Host "==============================================="
Write-Host " ILP MEGA-SCRIPT FULL BACKEND COMPLETADO"
Write-Host " Archivos CRUD base generados en:"
Write-Host "  - auth-service"
Write-Host "  - tenant-service"
Write-Host "  - assessment-service"
Write-Host "  - notification-service"
Write-Host "  - monitoring-service"
Write-Host "  - report-service"
Write-Host " Ahora ejecuta: mvn clean package -am"
Write-Host "==============================================="
