package com.inclusive.adaptiveeducationservice.api.research;

import com.inclusive.adaptiveeducationservice.research.dto.AccountabilityResponsibilityResponse;
import com.inclusive.adaptiveeducationservice.research.service.AccountabilityResponsibilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/analytics/accountability")
@RequiredArgsConstructor
public class AccountabilityResponsibilityController {

    private final AccountabilityResponsibilityService accountabilityResponsibilityService;

    @GetMapping("/responsibility-preview")
    public ResponseEntity<AccountabilityResponsibilityResponse>
    responsibilityPreview() {

        return ResponseEntity.ok(
                accountabilityResponsibilityService.generateResponsibilityPreview()
        );
    }
}
