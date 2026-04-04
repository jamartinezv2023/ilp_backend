// Location: auth-service/src/main/java/com/inclusive/authservice/dto/authorization/RolePermissionDTO.java
package com.inclusive.authservice.dto.authorization;

import java.util.UUID;

/**
 * DTO used to expose permissions assigned to a role.
 * This prevents leaking internal JPA entities.
 */
public class RolePermissionDTO {

    private UUID permissionId;
    private String code;
    private String description;

    public RolePermissionDTO(
            UUID permissionId,
            String code,
            String description
    ) {
        this.permissionId = permissionId;
        this.code = code;
        this.description = description;
    }

    public UUID getPermissionId() {
        return permissionId;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
