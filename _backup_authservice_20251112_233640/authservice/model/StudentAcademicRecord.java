package com.inclusive.authservice.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Embeddable
@Getter
@Setter
public class StudentAcademicRecord {
    private Double averageGrade;
    private Double mathScore;
    private Double readingScore;
    private Double scienceScore;
    private Boolean isRepeatingGrade;
    private Double attendanceRate;
    private Double homeworkCompletionRate;
    private String behavioralNotes;
    private Integer disciplinaryActions;
}




