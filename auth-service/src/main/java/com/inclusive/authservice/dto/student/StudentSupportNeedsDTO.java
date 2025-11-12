package com.inclusive.authservice.dto.student;

import lombok.*;

import java.io.Serializable;

/**
 * DTO representing the student's support needs or accommodations.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentSupportNeedsDTO implements Serializable {

    private Boolean requiresSpecialAssistance;
    private String learningDisabilityType;
    private String physicalAccessibilityNeeds;
    private String preferredLearningStyle;
    private String additionalSupportNotes;
}







