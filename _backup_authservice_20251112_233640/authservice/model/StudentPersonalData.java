package com.inclusive.authservice.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Embeddable
@Getter
@Setter
public class StudentPersonalData {
    private String fullName;
    private LocalDate birthDate;
    private String gender;
    private String ethnicity;
    private String disabilityStatus;
    private String socioEconomicStatus;
    private String familyStructure;
    private String location;
    private String schoolLevel;
    private String guardianName;
    private String avatarUrl;
}






