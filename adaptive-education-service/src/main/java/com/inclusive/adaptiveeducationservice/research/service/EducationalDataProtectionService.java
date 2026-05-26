package com.inclusive.adaptiveeducationservice.research.service;

import com.inclusive.adaptiveeducationservice.research.dto.EducationalDataProtectionResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EducationalDataProtectionService {

    public EducationalDataProtectionResponse generatePrivacyPreview() {

        return new EducationalDataProtectionResponse(
                "HIGH_PROTECTION",
                "Educational sensitive data must be minimized, anonymized and accessed only under institutional authorization.",
                "REQUIRED",
                "ROLE_BASED_ACCESS_CONTROL",
                List.of(
                        "Student educational profile",
                        "Learning support signals",
                        "Intervention history",
                        "PIAR-oriented educational evidence",
                        "Vocational preference responses"
                ),
                "CONTROLLED"
        );
    }
}
