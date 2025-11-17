package com.inclusive.authservice.dto.student;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Resumen acadÃ©mico del estudiante (valores agregados).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resumen acadÃ©mico del estudiante")
public class StudentAcademicRecordDTO {

    @Schema(description = "Promedio general", example = "4.3")
    private Double averageGrade;

    @Schema(description = "Puntaje en matemÃ¡ticas", example = "85.0")
    private Double mathScore;

    @Schema(description = "Puntaje en lectura", example = "88.5")
    private Double readingScore;

    @Schema(description = "Puntaje en ciencias", example = "78.0")
    private Double scienceScore;

    @Schema(description = "Indicador si repite grado", example = "false")
    private Boolean isRepeatingGrade;

    @Schema(description = "Tasa de asistencia (0-100)", example = "95.0")
    private Double attendanceRate;

    @Schema(description = "Porcentaje de tareas completadas (0-100)", example = "90.0")
    private Double homeworkCompletionRate;

    @Schema(description = "Notas de comportamiento (texto libre)", example = "Buen comportamiento general")
    private String behavioralNotes;

    @Schema(description = "Acciones disciplinarias registradas", example = "Ninguna")
    private String disciplinaryActions;
}





