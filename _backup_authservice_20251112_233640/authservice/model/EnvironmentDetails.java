package com.inclusive.authservice.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;

/**
 * POJO tipado para describir el entorno del estudiante (device, internet, horarios, etc.)
 * Se serializa a JSONB en la columna `environmentJson`.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Environment details stored in JSONB for a student (typed POJO)")
public class EnvironmentDetails implements Serializable {

    @Schema(description = "Dispositivo principal (e.g., Mobile, Laptop)", example = "Mobile")
    private String deviceAccess;

    @Schema(description = "Tipo de acceso a internet (e.g., Mobile data, Broadband)", example = "Mobile data")
    private String internetAccess;

    @Schema(description = "Cantidad de hermanos en la misma instituciÃ³n", example = "1")
    private Integer siblingsInSchool;

    @Schema(description = "Horario preferido para estudiar (e.g., Morning, Evening)", example = "Evening")
    private String preferredStudyTime;

    // Extensible con otros campos
}





