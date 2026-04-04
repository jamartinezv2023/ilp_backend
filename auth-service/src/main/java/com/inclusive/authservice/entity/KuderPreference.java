package com.inclusive.authservice.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class KuderPreference {

    /**
     * Ej: SOCIAL, MECANICO, CIENTIFICO, ARTISTICO, LITERARIO, etc.
     */
    private String area;

    /**
     * Puntaje obtenido en esa Ã¡rea
     */
    private Integer score;
}
