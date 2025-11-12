package com.inclusive.authservice.service.impl;

import com.inclusive.authservice.model.User;
import com.inclusive.authservice.model.VerificationToken;
import com.inclusive.authservice.model.enums.AccountStatus;
import com.inclusive.authservice.repository.UserRepository;
import com.inclusive.authservice.repository.VerificationTokenRepository;
import com.inclusive.authservice.service.NotificationService;
import com.inclusive.authservice.service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationServiceImpl implements VerificationService {

    private final VerificationTokenRepository tokenRepo;
    private final UserRepository userRepo;
    private final NotificationService notification;

    @Override
    @Transactional
    public String generateAndSend(User user, String channel, String destination) {
        // invalida tokens previos activos del usuario (opcional)
        // tokenRepo.deleteAll(...)

        String token = UUID.randomUUID().toString();
        VerificationToken vt = VerificationToken.builder()
                .token(token)
                .channel(channel)
                .user(user)
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .consumed(false)
                .build();
        tokenRepo.save(vt);

        String body = "Tu cÃ³digo/URL de verificaciÃ³n es: " + token + "\n" +
                "Expira en 15 minutos. Si no solicitaste esto, ignora este mensaje.";
        if ("SMS".equalsIgnoreCase(channel) && destination != null && !destination.isBlank()) {
            notification.sendSms(destination, body);
        } else {
            // EMAIL por defecto
            notification.sendEmail(user.getEmail(), "VerificaciÃ³n de cuenta", body);
        }
        return token;
    }

    @Override
    @Transactional
    public boolean verifyToken(String token) {
        var vt = tokenRepo.findByToken(token).orElse(null);
        if (vt == null || vt.isConsumed() || vt.getExpiresAt().isBefore(LocalDateTime.now())) {
            return false;
        }
        var user = vt.getUser();
        user.setEmailVerified(true);
        user.setAccountStatus(AccountStatus.ACTIVE);
        userRepo.save(user);

        vt.setConsumed(true);
        tokenRepo.save(vt);
        return true;
    }
}



