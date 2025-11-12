package com.inclusive.authservice.dto.student;

import lombok.*;

import java.io.Serializable;

/**
 * DTO representing the student's learning environment.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentEnvironmentDTO implements Serializable {

    private String socioEconomicLevel;
    private String homeStudyConditions;
    private String parentalSupportLevel;
    private String accessToTechnology;
    private String internetConnectivity;
}







