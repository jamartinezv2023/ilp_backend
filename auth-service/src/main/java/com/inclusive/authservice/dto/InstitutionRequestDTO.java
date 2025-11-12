package com.inclusive.authservice.dto;

import lombok.Data;

/**
 * DTO para registrar una nueva instituciÃ³n.
 */
@Data
public class InstitutionRequestDTO {
    private String code;
    private String name;
    private String country;
    private String language;
    private String domain;
    private String logoUrl;
    private String settingsJson; // JSON con configuraciones personalizadas
}





