package com.inclusive.authservice.controller;

import com.inclusive.authservice.dto.mfa.TotpCodeDTO;
import com.inclusive.authservice.dto.mfa.TotpRotateRequest;
import com.inclusive.authservice.dto.mfa.TotpSetupResponse;
import com.inclusive.authservice.model.User;
import com.inclusive.authservice.repository.UserRepository;
import com.inclusive.authservice.service.TotpService;
import com.inclusive.authservice.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/mfa/totp")
@RequiredArgsConstructor
public class MfaTotpController {

    private final TotpService totpService;
    private final UserRepository userRepository;

    private User me() {
        String username = SecurityUtils.currentUsername();
        if (username == null) throw new IllegalStateException("No autenticado");
        return userRepository.findByUsername(username)
                .orElseGet(() -> userRepository.findByEmail(username).orElseThrow(() ->
                        new IllegalStateException("Usuario no encontrado: " + username)));
    }

    @PostMapping("/enable")
    public ResponseEntity<TotpSetupResponse> enable() {
        return ResponseEntity.ok(totpService.enable(me()));
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody @Valid TotpCodeDTO body) {
        boolean ok = totpService.verify(me(), body.getCode());
        return ok ? ResponseEntity.ok().body("{\"message\":\"TOTP verificado y habilitado\"}")
                  : ResponseEntity.badRequest().body("{\"error\":\"CÃƒÂ³digo invÃƒÂ¡lido\"}");
    }

    @PostMapping("/disable")
    public ResponseEntity<?> disable(@RequestBody @Valid TotpCodeDTO body) {
        boolean ok = totpService.disable(me(), body.getCode());
        return ok ? ResponseEntity.ok().body("{\"message\":\"TOTP deshabilitado\"}")
                  : ResponseEntity.badRequest().body("{\"error\":\"CÃƒÂ³digo invÃƒÂ¡lido\"}");
    }

    @PostMapping("/rotate")
    public ResponseEntity<?> rotate(@RequestBody @Valid TotpRotateRequest body) {
        TotpSetupResponse res = totpService.rotate(me(), body.getCurrentCode());
        return ResponseEntity.ok(res);
    }
}



