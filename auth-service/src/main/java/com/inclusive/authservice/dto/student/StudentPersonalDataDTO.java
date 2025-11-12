package com.inclusive.authservice.dto.student;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Datos personales bÃ¡sicos del estudiante.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos personales bÃ¡sicos del estudiante")
public class StudentPersonalDataDTO {

    @Schema(description = "Nombre completo", example = "JosÃ© MartÃ­nez")
    @NotBlank
    @Size(max = 200)
    private String fullName;

    @Schema(description = "Fecha de nacimiento (ISO-8601)", example = "2008-05-21")
    private LocalDate birthDate;

    @Schema(description = "GÃ©nero", example = "MALE")
    @Size(max = 32)
    private String gender;

    @Schema(description = "Etnia o grupo Ã©tnico", example = "Mestizo")
    @Size(max = 100)
    private String ethnicity;

    @Schema(description = "Estado de discapacidad", example = "NO")
    @Size(max = 50)
    private String disabilityStatus;

    @Schema(description = "Estado socioeconÃ³mico (categorÃ­a)", example = "LOW")
    @Size(max = 50)
    private String socioEconomicStatus;

    @Schema(description = "Estructura familiar", example = "Monoparental")
    @Size(max = 150)
    private String familyStructure;

    @Schema(description = "UbicaciÃ³n / ciudad", example = "BogotÃ¡, Colombia")
    @Size(max = 200)
    private String location;

    @Schema(description = "Nivel escolar (grado)", example = "9")
    private String schoolLevel;

    @Schema(description = "Nombre del/la acudiente", example = "MarÃ­a LÃ³pez")
    @Size(max = 150)
    private String guardianName;

    @Schema(description = "URL de avatar", example = "https://cdn.example.com/avatars/jose.jpg")
    @Size(max = 500)
    private String avatarUrl;
}






