package com.inclusive.adaptiveeducationservice.research.service;

import com.inclusive.adaptiveeducationservice.research.dto.EducationalRiskManagementResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EducationalRiskManagementService {

    public EducationalRiskManagementResponse generateEducationalRiskManagement() {

        return new EducationalRiskManagementResponse(
                "CONTROLLED_RISK_MANAGEMENT",
                "MONITORED",
                "CONTROLLED",
                "CONTROLLED",
                List.of(
                        "Human oversight required",
                        "Clinical label exclusion",
                        "Teacher review before intervention",
                        "Institutional audit trail enabled"
                ),
                "Escalate to institutional support team when risk indicators exceed educational thresholds."
        );
    }
}
