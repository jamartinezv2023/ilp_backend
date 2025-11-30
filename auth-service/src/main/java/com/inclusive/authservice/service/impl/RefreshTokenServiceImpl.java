package com.inclusive.authservice.service.impl;

import com.inclusive.authservice.entity.RefreshToken;
import com.inclusive.authservice.repository.RefreshTokenRepository;
import com.inclusive.authservice.service.RefreshTokenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * ImplementaciÃ³n por defecto de RefreshTokenService.
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
            // Opcional: guardar razÃ³n en replacedByToken mientras no haya otro campo.
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