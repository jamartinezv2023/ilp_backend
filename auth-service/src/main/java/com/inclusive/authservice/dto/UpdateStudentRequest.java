// ðŸ“Œ UpdateStudentRequest.java
package com.inclusive.authservice.dto;

import com.inclusive.authservice.model.enums.AccountStatus;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class UpdateStudentRequest {

    @NotBlank(message = "El nombre completo es obligatorio")
    private String fullName;

    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    private LocalDate birthDate;

    private String gender;
    private String ethnicity;
    private String avatarUrl;

    private String location;
    private String guardianName;

    private String learningStyleKolb;
    private String learningStyleFelder;
    private String vocationalProfileKuder;
    private List<String> interests;

    private Double averageGrade;
    private Double mathScore;
    private Double readingScore;
    private Double scienceScore;

    private String deviceAccess;
    private String internetAccess;

    private AccountStatus accountStatus;
}





