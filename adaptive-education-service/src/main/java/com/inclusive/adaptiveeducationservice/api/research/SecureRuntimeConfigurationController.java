package com.inclusive.adaptiveeducationservice.api.research;

import com.inclusive.adaptiveeducationservice.research.dto.SecureRuntimeConfigurationResponse;
import com.inclusive.adaptiveeducationservice.research.service.SecureRuntimeConfigurationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/security")
@RequiredArgsConstructor
public class SecureRuntimeConfigurationController {

    private final SecureRuntimeConfigurationService secureRuntimeConfigurationService;

    @GetMapping("/runtime-config-preview")
    public ResponseEntity<SecureRuntimeConfigurationResponse>
    runtimeConfigPreview() {

        return ResponseEntity.ok(
                secureRuntimeConfigurationService.generateRuntimeConfigPreview()
        );
    }
}
