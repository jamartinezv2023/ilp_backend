package com.inclusive.authservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermissionRequestDTO {

    @NotBlank(message = "El nombre del permiso es obligatorio")
    private String name;

    private String description;
}




