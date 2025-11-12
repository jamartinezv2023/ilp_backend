package com.inclusive.authservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;

/**
 * POJO tipado que representa las necesidades de apoyo del estudiante.
 * Se serializa a JSONB en la columna `needsJson`.
 *
 * - Extensible: si en el futuro se aÃ±aden campos, Jackson ignorarÃ¡ los desconocidos (ignoreUnknown = true).
 * - Tipado: facilita validaciÃ³n y uso en IDE.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Support details stored in JSONB for a student (typed POJO)")
public class SupportDetails implements Serializable {

    @Schema(description = "Indica si recibe apoyo psicolÃ³gico", example = "true")
    private Boolean receivesPsychologicalSupport;

    @Schema(description = "Indica si recibe apoyo de educaciÃ³n especial", example = "false")
    private Boolean receivesSpecialEducationSupport;

    // Campo adicional de ejemplo: tipo de adaptaciones requeridas
    @Schema(description = "InformaciÃ³n adicional sobre adaptaciones", example = "{\"extraTime\": true, \"assistiveTech\": \"screenReader\"}")
    private Object accommodations; // Usamos Object para mantener flexibilidad (puede ser Map)
}




