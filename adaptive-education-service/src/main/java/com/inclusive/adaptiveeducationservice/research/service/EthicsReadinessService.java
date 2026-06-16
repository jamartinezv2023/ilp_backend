package com.inclusive.adaptiveeducationservice.research.service;

import com.inclusive.adaptiveeducationservice.research.dto.EthicsReadinessResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EthicsReadinessService {

    public EthicsReadinessResponse generateEthicsReadinessPreview() {

        return new EthicsReadinessResponse(
                "READY_FOR_ETHICS_SUBMISSION_PREPARATION",
                "MANDATORY",
                "INFORMED_CONSENT_REQUIRED",
                "DATA_PROTECTION_GOVERNANCE_REQUIRED",
                "LOW_CONTROLLED",
                "CONTROLLED_WITH_ANONYMIZATION_AND_MINIMIZATION",
                "PREPARATION",
                List.of(
                        "Non-clinical educational AI boundary defined",
                        "Human oversight required",
                        "No clinical diagnosis produced",
                        "Explainability and traceability available",
                        "Fairness and bias assessment available",
                        "Data protection preview implemented",
                        "Pilot validation requires informed consent"
                ),
                "Prepare ethics committee documentation including informed consent, anonymization protocol, data minimization, human oversight and non-clinical AI safeguards."
        );
    }
}
