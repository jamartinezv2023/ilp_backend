param(
    [string]$ProjectRoot = "."
)

$ErrorActionPreference = "Stop"

function New-FileWithBackup {
    param(
        [Parameter(Mandatory = $true)][string]$Path,
        [Parameter(Mandatory = $true)][string]$Content
    )

    $fullPath = Join-Path -Path $ProjectRoot -ChildPath $Path
    $dir = Split-Path $fullPath -Parent

    if (-not (Test-Path $dir)) {
        New-Item -ItemType Directory -Path $dir -Force | Out-Null
    }

    if (Test-Path $fullPath) {
        $timestamp = Get-Date -Format "yyyyMMddHHmmss"
        $backupPath = "$fullPath.bak_$timestamp"
        Copy-Item $fullPath $backupPath -Force
        Write-Host "Backup creado: $backupPath"
    }

    # UTF8 sin BOM para evitar caracteres invisibles raros
    $utf8NoBom = New-Object System.Text.UTF8Encoding($false)
    [System.IO.File]::WriteAllText($fullPath, $Content, $utf8NoBom)

    Write-Host "Archivo actualizado: $fullPath"
}

# ============================================================
# 1) Entidad JPA: RefreshToken
# ============================================================

$refreshTokenEntity = @'
package com.inclusive.authservice.entity;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * Entidad RefreshToken persistida en PostgreSQL.
 * Pensada para:
 *  - Logout real (revocación de tokens)
 *  - Rotación segura
 *  - Auditoría y anti-replay
 */
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token", nullable = false, unique = true, length = 512)
    private String token;

    /**
     * ID del usuario al que pertenece el token.
     * Apunta a la PK de la tabla de usuarios (ej: users.id).
     * Se usa como Long para no acoplarnos a una implementación concreta.
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "created_by_ip", length = 50)
    private String createdByIp;

    @Column(name = "revoked", nullable = false)
    private boolean revoked = false;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @Column(name = "revoked_by_ip", length = 50)
    private String revokedByIp;

    /**
     * Token que reemplazó a este (para rotación segura).
     */
    @Column(name = "replaced_by_token", length = 512)
    private String replacedByToken;

    public RefreshToken() {
    }

    // ======================
    // Getters y setters
    // ======================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedByIp() {
        return createdByIp;
    }

    public void setCreatedByIp(String createdByIp) {
        this.createdByIp = createdByIp;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    public Instant getRevokedAt() {
        return revokedAt;
    }

    public void setRevokedAt(Instant revokedAt) {
        this.revokedAt = revokedAt;
    }

    public String getRevokedByIp() {
        return revokedByIp;
    }

    public void setRevokedByIp(String revokedByIp) {
        this.revokedByIp = revokedByIp;
    }

    public String getReplacedByToken() {
        return replacedByToken;
    }

    public void setReplacedByToken(String replacedByToken) {
        this.replacedByToken = replacedByToken;
    }
}
'@

New-FileWithBackup -Path "src/main/java/com/inclusive/authservice/entity/RefreshToken.java" -Content $refreshTokenEntity

# ============================================================
# 2) Repositorio: RefreshTokenRepository
# ============================================================

$refreshTokenRepository = @'
package com.inclusive.authservice.repository;

import com.inclusive.authservice.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para refresh tokens.
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findByUserIdAndRevokedFalse(Long userId);

    void deleteByUserId(Long userId);
}
'@

New-FileWithBackup -Path "src/main/java/com/inclusive/authservice/repository/RefreshTokenRepository.java" -Content $refreshTokenRepository

# ============================================================
# 3) Servicio: RefreshTokenService (interfaz)
# ============================================================

$refreshTokenService = @'
package com.inclusive.authservice.service;

import com.inclusive.authservice.entity.RefreshToken;

import java.time.Instant;

/**
 * Servicio para gestionar refresh tokens persistidos.
 */
public interface RefreshTokenService {

    RefreshToken createRefreshToken(Long userId,
                                    String token,
                                    Instant expiresAt,
                                    String createdByIp);

    void revokeToken(String token,
                     String revokedByIp,
                     String reason);

    void revokeAllUserTokens(Long userId,
                             String revokedByIp,
                             String reason);

    boolean isValid(String token);
}
'@

New-FileWithBackup -Path "src/main/java/com/inclusive/authservice/service/RefreshTokenService.java" -Content $refreshTokenService

# ============================================================
# 4) Implementación: RefreshTokenServiceImpl
# ============================================================

$refreshTokenServiceImpl = @'
package com.inclusive.authservice.service.impl;

import com.inclusive.authservice.entity.RefreshToken;
import com.inclusive.authservice.repository.RefreshTokenRepository;
import com.inclusive.authservice.service.RefreshTokenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Implementación por defecto de RefreshTokenService.
 */
@Service
@Transactional
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository repository;

    public RefreshTokenServiceImpl(RefreshTokenRepository repository) {
        this.repository = repository;
    }

    @Override
    public RefreshToken createRefreshToken(Long userId,
                                           String token,
                                           Instant expiresAt,
                                           String createdByIp) {
        RefreshToken rt = new RefreshToken();
        rt.setUserId(userId);
        rt.setToken(token);
        rt.setExpiresAt(expiresAt);
        rt.setCreatedAt(Instant.now());
        rt.setCreatedByIp(createdByIp);
        return repository.save(rt);
    }

    @Override
    public void revokeToken(String token,
                            String revokedByIp,
                            String reason) {
        repository.findByToken(token).ifPresent(rt -> {
            rt.setRevoked(true);
            rt.setRevokedAt(Instant.now());
            rt.setRevokedByIp(revokedByIp);
            // Opcional: guardar razón en replacedByToken mientras no haya otro campo.
            if (reason != null && !reason.isEmpty()) {
                rt.setReplacedByToken(reason);
            }
        });
    }

    @Override
    public void revokeAllUserTokens(Long userId,
                                    String revokedByIp,
                                    String reason) {
        List<RefreshToken> tokens = repository.findByUserIdAndRevokedFalse(userId);
        Instant now = Instant.now();
        for (RefreshToken rt : tokens) {
            rt.setRevoked(true);
            rt.setRevokedAt(now);
            rt.setRevokedByIp(revokedByIp);
            if (reason != null && !reason.isEmpty()) {
                rt.setReplacedByToken(reason);
            }
        }
    }

    @Override
    public boolean isValid(String token) {
        return repository.findByToken(token)
                .filter(rt -> !rt.isRevoked())
                .filter(rt -> rt.getExpiresAt() != null && rt.getExpiresAt().isAfter(Instant.now()))
                .isPresent();
    }
}
'@

New-FileWithBackup -Path "src/main/java/com/inclusive/authservice/service/impl/RefreshTokenServiceImpl.java" -Content $refreshTokenServiceImpl

Write-Host ""
Write-Host "============================================================"
Write-Host "  Persistencia de refresh tokens creada/actualizada."
Write-Host "  Ahora puedes:"
Write-Host "   - Adaptar AuthServiceImpl para usar RefreshTokenService"
Write-Host "   - Exponer endpoints /auth/refresh y /auth/logout"
Write-Host "============================================================"
