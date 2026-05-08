package com.inclusive.common.events.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentCompletedEvent {
    private Long studentId;
    private Long assessmentId;
    private String tenantId;
    private String type;
    private Double score;
    private String result;
    private OffsetDateTime completedAt;
    private String correlationId;

    // MÃ©todos estilo Record para compatibilidad
    public Long studentId() { return studentId; }
    public Long assessmentId() { return assessmentId; }
    public String tenantId() { return tenantId; }
    public String type() { return type; }
    public Double score() { return score; }
    public String result() { return result; }
    public OffsetDateTime completedAt() { return completedAt; }
    public String correlationId() { return correlationId; }
}
