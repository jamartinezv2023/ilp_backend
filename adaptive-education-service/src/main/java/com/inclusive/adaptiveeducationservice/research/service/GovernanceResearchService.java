package com.inclusive.adaptiveeducationservice.research.service;

import com.inclusive.adaptiveeducationservice.research.dto.GovernancePolicyPreviewResponse;
import org.springframework.stereotype.Service;

@Service
public class GovernanceResearchService {

    public GovernancePolicyPreviewResponse generateGovernancePreview() {

        return new GovernancePolicyPreviewResponse(

                "INSTITUTIONAL",

                "NON_CLINICAL_EDUCATIONAL_AI",

                "ACTIVE",

                "ENABLED",

                "REQUIRED",

                "CONTROLLED"
        );
    }
}
