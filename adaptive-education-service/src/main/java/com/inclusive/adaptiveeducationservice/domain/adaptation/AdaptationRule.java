package com.inclusive.adaptiveeducationservice.domain.adaptation;
import jakarta.persistence.*;

@Entity
@Table(name = "adaptation_rules")
public class AdaptationRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String styleKey;
    private String styleValue;
    private String adjustmentContent;

    public AdaptationRule() {}
    public String getAdjustmentContent() { return adjustmentContent; }
    public void setAdjustmentContent(String content) { this.adjustmentContent = content; }
}
