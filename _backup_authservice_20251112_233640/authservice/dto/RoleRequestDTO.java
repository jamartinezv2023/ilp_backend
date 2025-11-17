package com.inclusive.authservice.dto;

import com.inclusive.authservice.model.RoleType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;

@Data
public class RoleRequestDTO {
    @NotNull
    private RoleType name;

    // Lista de IDs de permisos
    private Set<Long> permissionIds;
}





