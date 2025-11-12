package com.inclusive.authservice.dto.student;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Perfil de aprendizaje: contiene JSON crudo y scores derivados.
 * Mantiene compatibilidad con la entidad que almacena rawResponsesJson / scoresJson.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Perfil de aprendizaje (rawResponsesJson / scoresJson)")
public class StudentLearningProfileDTO {

    @Schema(description = "ID del perfil de aprendizaje", example = "11")
    private Long id;

    @Schema(description = "JSON con respuestas crudas de cuestionarios", example = "{\"kolb\": {...}}")
    private String rawResponsesJson;

    @Schema(description = "JSON con scores/valores derivados", example = "{\"kolb_score\": 0.72}")
    private String scoresJson;

    @Schema(description = "VersiÃ³n del perfil (actualizaciones)", example = "1")
    private Integer version;

    @Schema(description = "Fecha de creaciÃ³n", example = "2025-10-12T08:00:00")
    private LocalDateTime createdAt;
}






