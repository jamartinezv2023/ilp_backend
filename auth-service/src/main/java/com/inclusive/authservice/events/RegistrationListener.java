package com.inclusive.authservice.events;

import com.inclusive.authservice.model.User;
import com.inclusive.authservice.service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegistrationListener {

    private final VerificationService verificationService;

    @EventListener
    public void handleRegistration(OnRegistrationCompleteEvent event) {
        User user = event.getUser();
        verificationService.generateAndSend(user, "EMAIL", user.getEmail());
    }
}



