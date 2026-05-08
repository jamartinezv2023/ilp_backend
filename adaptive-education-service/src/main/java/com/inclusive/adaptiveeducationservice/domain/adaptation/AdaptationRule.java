package com.inclusive.adaptiveeducationservice.domain.adaptation;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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
