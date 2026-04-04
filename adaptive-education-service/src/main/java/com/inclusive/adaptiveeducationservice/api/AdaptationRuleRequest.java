package com.inclusive.adaptiveeducationservice.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AdaptationRuleRequest {

    @NotBlank
    public String tenantId;

    @NotBlank
    @Size(max = 120)
    public String name;

    @NotBlank
    @Size(max = 500)
    public String conditionExpression;

    @NotBlank
    @Size(max = 500)
    public String actionExpression;

    public boolean enabled = true;
}
