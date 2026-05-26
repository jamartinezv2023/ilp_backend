package com.inclusive.adaptiveeducationservice.research.service;

import com.inclusive.adaptiveeducationservice.research.dto.AccountabilityResponsibilityResponse;
import org.springframework.stereotype.Service;

@Service
public class AccountabilityResponsibilityService {

    public AccountabilityResponsibilityResponse generateResponsibilityPreview() {

        return new AccountabilityResponsibilityResponse(
                "INSTITUTIONAL_ACCOUNTABILITY",
                "TEACHER_AND_INSTITUTIONAL_SUPPORT_TEAM",
                "ASSISTIVE_RECOMMENDATION_ONLY",
                "HUMAN_EDUCATIONAL_AUTHORITY",
                "MANDATORY",
                "DEFINED"
        );
    }
}
