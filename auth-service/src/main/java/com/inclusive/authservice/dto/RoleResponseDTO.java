package com.inclusive.authservice.dto;

import com.inclusive.authservice.model.RoleType;
import lombok.Data;

import java.util.Set;

@Data
public class RoleResponseDTO {
    private Long id;
    private RoleType name;
    private Set<String> permissions; // nombres legibles de permisos
}





