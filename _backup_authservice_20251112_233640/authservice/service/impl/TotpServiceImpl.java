package com.inclusive.authservice.service.impl;

import com.inclusive.authservice.dto.mfa.TotpSetupResponse;
import com.inclusive.authservice.model.User;
import com.inclusive.authservice.model.UserMfa;
import com.inclusive.authservice.repository.UserMfaRepository;
import com.inclusive.authservice.service.TotpService;
import com.inclusive.authservice.util.TotpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TotpServiceImpl implements TotpService {

    private final UserMfaRepository userMfaRepository;

    // ParÃ¡metros TOTP canÃ³nicos
    private static final int DIGITS = 6;
    private static final int PERIOD = 30;
    private static final int WINDOW = 1; // +/- 1 paso

    @Override
    @Transactional
    public TotpSetupResponse enable(User user) {
        UserMfa mfa = userMfaRepository.findByUser(user).orElseGet(() ->
                UserMfa.builder().user(user).build()
        );

        String issuer = "InclusivePlatform";
        String label  = user.getUsername() != null ? user.getUsername() : user.getEmail();
        String secret = TotpUtil.generateSecretBase32(20);
        String otpauth = TotpUtil.buildOtpAuthUri(issuer, label, secret, DIGITS, PERIOD);
        String qr = TotpUtil.qrFromOtpAuth(otpauth);

        mfa.setTotpSecret(secret);
        mfa.setIssuer(issuer);
        mfa.setLabel(label);
        // todavÃ­a no marcamos enabled hasta que verifique al menos un cÃ³digo
        mfa.setMfaEnabled(false);
        userMfaRepository.save(mfa);

        return new TotpSetupResponse(secret, otpauth, qr);
    }

    @Override
    @Transactional
    public boolean verify(User user, String code) {
        UserMfa mfa = userMfaRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("MFA no inicializado para el usuario"));
        boolean ok = TotpUtil.verifyCode(mfa.getTotpSecret(), code, DIGITS, PERIOD, WINDOW);
        if (ok && !mfa.isMfaEnabled()) {
            mfa.setMfaEnabled(true);
            mfa.setLastEnabledAt(LocalDateTime.now());
            userMfaRepository.save(mfa);
        }
        return ok;
    }

    @Override
    @Transactional
    public boolean disable(User user, String currentCode) {
        UserMfa mfa = userMfaRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("MFA no inicializado para el usuario"));
        if (!mfa.isMfaEnabled()) return true; // ya estaba deshabilitado
        boolean ok = TotpUtil.verifyCode(mfa.getTotpSecret(), currentCode, DIGITS, PERIOD, WINDOW);
        if (!ok) return false;
        mfa.setMfaEnabled(false);
        mfa.setTotpSecret(null);
        mfa.setLastDisabledAt(LocalDateTime.now());
        userMfaRepository.save(mfa);
        return true;
    }

    @Override
    @Transactional
    public TotpSetupResponse rotate(User user, String currentCode) {
        UserMfa mfa = userMfaRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("MFA no inicializado para el usuario"));
        if (!mfa.isMfaEnabled())
            throw new IllegalStateException("MFA debe estar habilitado para rotar el secreto");

        boolean ok = TotpUtil.verifyCode(mfa.getTotpSecret(), currentCode, DIGITS, PERIOD, WINDOW);
        if (!ok) throw new IllegalArgumentException("CÃ³digo TOTP invÃ¡lido");

        String secret = TotpUtil.generateSecretBase32(20);
        String issuer = mfa.getIssuer() != null ? mfa.getIssuer() : "InclusivePlatform";
        String label  = mfa.getLabel()  != null ? mfa.getLabel()  : user.getUsername();
        String otpauth = TotpUtil.buildOtpAuthUri(issuer, label, secret, DIGITS, PERIOD);
        String qr = TotpUtil.qrFromOtpAuth(otpauth);

        mfa.setTotpSecret(secret);
        mfa.setLastRotatedAt(LocalDateTime.now());
        userMfaRepository.save(mfa);

        return new TotpSetupResponse(secret, otpauth, qr);
    }
}



