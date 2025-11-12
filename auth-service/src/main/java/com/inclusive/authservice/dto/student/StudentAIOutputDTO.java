package com.inclusive.authservice.dto.student;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * RepresentaciÃ³n de una salida / artefacto generada por un modelo IA para un estudiante.
 * DiseÃ±ado para ser compatible con la entidad StudentAIOutput (jsonb + metadatos).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Salida / artefacto generado por modelos de IA")
public class StudentAIOutputDTO {

    @Schema(description = "ID del artefacto IA", example = "501")
    private Long id;

    @Schema(description = "Nombre del modelo que produjo la salida", example = "recommender-v1")
    private String modelName;

    @Schema(description = "VersiÃ³n del modelo", example = "1.0.0")
    private String modelVersion;

    @Schema(description = "ID del vector en el vector DB (Milvus/FAISS/Qdrant)", example = "vec_000123")
    private String milvusVectorId;

    @Schema(description = "ID del nodo relacionado en Neo4j", example = "node_abc123")
    private String neo4jNodeId;

    @Schema(description = "ExplicaciÃ³n o metadatos en JSON (string)", example = "{\"reason\":\"low_mastery\",\"evidence\":{}}")
    private String explanationJson;

    @Schema(description = "Puntaje o confianza (0..1) asociado a la salida", example = "0.82")
    private Double score;

    @Schema(description = "Fecha de creaciÃ³n del artefacto (ISO-8601)", example = "2025-10-12T09:30:00")
    private LocalDateTime createdAt;
}






