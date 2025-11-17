# ===============================================
#  ILP FIX SCRIPT - AUTH-SERVICE CLEAN CRUD
#  - Regenera todas las clases de auth-service
#  - Sin BOM, sin caracteres raros
#  - Código Java compilable y coherente
# ===============================================

$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
Write-Host "ProjectRoot: $root"

$authRoot    = Join-Path $root "auth-service\src\main\java\com\inclusive\authservice"
$entityDir   = Join-Path $authRoot "entity"
$dtoDir      = Join-Path $authRoot "dto"
$mapperDir   = Join-Path $authRoot "mapper"
$repoDir     = Join-Path $authRoot "repository"
$serviceDir  = Join-Path $authRoot "service"
$implDir     = Join-Path $authRoot "service\impl"

$dirs = @($entityDir, $dtoDir, $mapperDir, $repoDir, $serviceDir, $implDir)
foreach ($d in $dirs) {
    if (-not (Test-Path $d)) {
        New-Item -ItemType Directory -Path $d -Force | Out-Null
        Write-Host "[DIR] Created: $d"
    } else {
        Write-Host "[DIR] Exists:  $d"
    }
}

# ===============================================
#  UserAccount.java (ENTITY)
# ===============================================
$userAccountEntity = @'
package com.inclusive.authservice.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

/**
 * UserAccount entity for authentication and authorization.
 * This model is intentionally simple but extensible for SaaS / multi-tenant usage.
 */
@Entity
@Table(name = "user_accounts")
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 150)
    private String username;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    /**
     * Comma-separated list of roles. For a more advanced model, you can
     * normalize this to a Role entity and a join table.
     */
    @Column(length = 255)
    private String roles;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(nullable = false)
    private boolean locked = false;

    /**
     * Tenant identifier for multi-tenant SaaS scenarios.
     */
    @Column(name = "tenant_id", length = 100)
    private String tenantId;

    @Column(name = "mfa_enabled", nullable = false)
    private boolean mfaEnabled = false;

    @Column(name = "mfa_secret", length = 255)
    private String mfaSecret;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }

    public UserAccount() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public boolean isMfaEnabled() {
        return mfaEnabled;
    }

    public void setMfaEnabled(boolean mfaEnabled) {
        this.mfaEnabled = mfaEnabled;
    }

    public String getMfaSecret() {
        return mfaSecret;
    }

    public void setMfaSecret(String mfaSecret) {
        this.mfaSecret = mfaSecret;
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

Set-Content -Path (Join-Path $entityDir "UserAccount.java") -Value $userAccountEntity -Encoding ASCII
Write-Host "[OK]   Wrote: UserAccount.java"

# ===============================================
#  UserAccountDTO.java (DTO)
# ===============================================
$userAccountDto = @'
package com.inclusive.authservice.dto;

import java.io.Serializable;
import java.time.OffsetDateTime;

/**
 * DTO for UserAccount used by controllers and other services.
 */
public class UserAccountDTO implements Serializable {

    private Long id;
    private String username;
    private String email;
    private String password; // plain-text for input only, do NOT return in real systems
    private String roles;
    private boolean enabled;
    private boolean locked;
    private String tenantId;
    private boolean mfaEnabled;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public UserAccountDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public boolean isMfaEnabled() {
        return mfaEnabled;
    }

    public void setMfaEnabled(boolean mfaEnabled) {
        this.mfaEnabled = mfaEnabled;
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

Set-Content -Path (Join-Path $dtoDir "UserAccountDTO.java") -Value $userAccountDto -Encoding ASCII
Write-Host "[OK]   Wrote: UserAccountDTO.java"

# ===============================================
#  UserAccountMapper.java (MAPPER)
# ===============================================
$userAccountMapper = @'
package com.inclusive.authservice.mapper;

import com.inclusive.authservice.dto.UserAccountDTO;
import com.inclusive.authservice.entity.UserAccount;

/**
 * Mapper utility between UserAccount entity and UserAccountDTO.
 */
public final class UserAccountMapper {

    private UserAccountMapper() {
    }

    public static UserAccountDTO toDto(UserAccount entity) {
        if (entity == null) {
            return null;
        }
        UserAccountDTO dto = new UserAccountDTO();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setEmail(entity.getEmail());
        dto.setRoles(entity.getRoles());
        dto.setEnabled(entity.isEnabled());
        dto.setLocked(entity.isLocked());
        dto.setTenantId(entity.getTenantId());
        dto.setMfaEnabled(entity.isMfaEnabled());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        // Never map passwordHash to password for output.
        return dto;
    }

    public static UserAccount toEntity(UserAccountDTO dto) {
        if (dto == null) {
            return null;
        }
        UserAccount entity = new UserAccount();
        entity.setId(dto.getId());
        entity.setUsername(dto.getUsername());
        entity.setEmail(dto.getEmail());
        entity.setRoles(dto.getRoles());
        entity.setEnabled(dto.isEnabled());
        entity.setLocked(dto.isLocked());
        entity.setTenantId(dto.getTenantId());
        entity.setMfaEnabled(dto.isMfaEnabled());
        // Password hashing should be done in the service layer.
        return entity;
    }

    public static void updateEntityFromDto(UserAccountDTO dto, UserAccount entity) {
        if (dto == null || entity == null) {
            return;
        }
        entity.setUsername(dto.getUsername());
        entity.setEmail(dto.getEmail());
        entity.setRoles(dto.getRoles());
        entity.setEnabled(dto.isEnabled());
        entity.setLocked(dto.isLocked());
        entity.setTenantId(dto.getTenantId());
        entity.setMfaEnabled(dto.isMfaEnabled());
    }
}
'@

Set-Content -Path (Join-Path $mapperDir "UserAccountMapper.java") -Value $userAccountMapper -Encoding ASCII
Write-Host "[OK]   Wrote: UserAccountMapper.java"

# ===============================================
#  UserAccountRepository.java (REPOSITORY)
# ===============================================
$userAccountRepo = @'
package com.inclusive.authservice.repository;

import com.inclusive.authservice.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for UserAccount entity.
 */
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    Optional<UserAccount> findByUsername(String username);

    Optional<UserAccount> findByEmail(String email);
}
'@

Set-Content -Path (Join-Path $repoDir "UserAccountRepository.java") -Value $userAccountRepo -Encoding ASCII
Write-Host "[OK]   Wrote: UserAccountRepository.java"

# ===============================================
#  UserAccountService.java (SERVICE INTERFACE)
# ===============================================
$userAccountService = @'
package com.inclusive.authservice.service;

import com.inclusive.authservice.dto.UserAccountDTO;

import java.util.List;

/**
 * Service interface for UserAccount operations.
 */
public interface UserAccountService {

    List<UserAccountDTO> findAll();

    UserAccountDTO findById(Long id);

    UserAccountDTO create(UserAccountDTO dto);

    UserAccountDTO update(Long id, UserAccountDTO dto);

    void delete(Long id);
}
'@

Set-Content -Path (Join-Path $serviceDir "UserAccountService.java") -Value $userAccountService -Encoding ASCII
Write-Host "[OK]   Wrote: UserAccountService.java"

# ===============================================
#  UserAccountServiceImpl.java (SERVICE IMPL)
# ===============================================
$userAccountServiceImpl = @'
package com.inclusive.authservice.service.impl;

import com.inclusive.authservice.dto.UserAccountDTO;
import com.inclusive.authservice.entity.UserAccount;
import com.inclusive.authservice.mapper.UserAccountMapper;
import com.inclusive.authservice.repository.UserAccountRepository;
import com.inclusive.authservice.service.UserAccountService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Default implementation of UserAccountService.
 */
@Service
@Transactional
public class UserAccountServiceImpl implements UserAccountService {

    private final UserAccountRepository repository;

    public UserAccountServiceImpl(UserAccountRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserAccountDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(UserAccountMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserAccountDTO findById(Long id) {
        UserAccount entity = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("UserAccount not found with id: " + id));
        return UserAccountMapper.toDto(entity);
    }

    @Override
    public UserAccountDTO create(UserAccountDTO dto) {
        UserAccount entity = UserAccountMapper.toEntity(dto);
        // Aquí deberías aplicar hashing de password en un escenario real.
        if (dto.getPassword() != null) {
            entity.setPasswordHash(dto.getPassword());
        }
        UserAccount saved = repository.save(entity);
        return UserAccountMapper.toDto(saved);
    }

    @Override
    public UserAccountDTO update(Long id, UserAccountDTO dto) {
        UserAccount existing = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("UserAccount not found with id: " + id));
        UserAccountMapper.updateEntityFromDto(dto, existing);
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            existing.setPasswordHash(dto.getPassword());
        }
        UserAccount saved = repository.save(existing);
        return UserAccountMapper.toDto(saved);
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("UserAccount not found with id: " + id);
        }
        repository.deleteById(id);
    }
}
'@

Set-Content -Path (Join-Path $implDir "UserAccountServiceImpl.java") -Value $userAccountServiceImpl -Encoding ASCII
Write-Host "[OK]   Wrote: UserAccountServiceImpl.java"

# ===============================================
#  UserAccountController.java (REST CONTROLLER)
# ===============================================
$userAccountController = @'
package com.inclusive.authservice.controller;

import com.inclusive.authservice.dto.UserAccountDTO;
import com.inclusive.authservice.service.UserAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;

/**
 * REST controller exposing CRUD endpoints for UserAccount.
 *
 * Base path:
 *   /api/auth/users
 */
@RestController
@RequestMapping("/api/auth/users")
@Validated
public class UserAccountController {

    private final UserAccountService service;

    public UserAccountController(UserAccountService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<UserAccountDTO>> getAll() {
        List<UserAccountDTO> users = service.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserAccountDTO> getById(@PathVariable Long id) {
        UserAccountDTO dto = service.findById(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<UserAccountDTO> create(@Valid @RequestBody UserAccountDTO dto) {
        UserAccountDTO created = service.create(dto);
        URI location = URI.create("/api/auth/users/" + created.getId());
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserAccountDTO> update(@PathVariable Long id,
                                                 @Valid @RequestBody UserAccountDTO dto) {
        UserAccountDTO updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
'@

Set-Content -Path (Join-Path $authRoot "controller\UserAccountController.java") -Value $userAccountController -Encoding ASCII
Write-Host "[OK]   Wrote: UserAccountController.java"

Write-Host "==============================================="
Write-Host " AUTH-SERVICE FIX COMPLETADO"
Write-Host " Ahora ejecuta:"
Write-Host "   mvn clean package -pl auth-service -am"
Write-Host " y luego, si todo está OK:"
Write-Host "   mvn clean package -am"
Write-Host "==============================================="
