package com.inclusive.adaptiveeducationservice.research.dto;

public record HumanCenteredAssistanceResponse(

        String humanCenteredLevel,

        String teacherDecisionAuthority,

        String aiAutonomyLevel,

        String humanInTheLoop,

        String pedagogicalCollaboration,

        String educationalDecisionSupport
) {
}
