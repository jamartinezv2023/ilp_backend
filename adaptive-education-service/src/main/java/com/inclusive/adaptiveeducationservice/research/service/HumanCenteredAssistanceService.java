package com.inclusive.adaptiveeducationservice.research.service;

import com.inclusive.adaptiveeducationservice.research.dto.HumanCenteredAssistanceResponse;
import org.springframework.stereotype.Service;

@Service
public class HumanCenteredAssistanceService {

    public HumanCenteredAssistanceResponse generateHumanCenteredAssistance() {

        return new HumanCenteredAssistanceResponse(

                "TEACHER_ASSISTED_AI",

                "PRESERVED",

                "LIMITED_ASSISTIVE",

                "MANDATORY",

                "ACTIVE",

                "ENABLED"
        );
    }
}
