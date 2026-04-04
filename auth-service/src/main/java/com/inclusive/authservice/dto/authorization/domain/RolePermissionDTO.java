package com.inclusive.authservice.dto.authorization.domain;

import java.util.UUID;

/**
 * DTO de lectura que representa la relaciÃ³n
 * Rol -> Permiso (proyecciÃ³n para API).
 */
public class RolePermissionDTO {

    private UUID roleId;
    private String roleName;

    private UUID permissionId;
    private String permissionCode;
    private String permissionDescription;

    /* ===== Getters & Setters ===== */

    public UUID getRoleId() {
        return roleId;
    }

    public void setRoleId(UUID roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public UUID getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(UUID permissionId) {
        this.permissionId = permissionId;
    }

    public String getPermissionCode() {
        return permissionCode;
    }

    public void setPermissionCode(String permissionCode) {
        this.permissionCode = permissionCode;
    }

    public String getPermissionDescription() {
        return permissionDescription;
    }

    public void setPermissionDescription(String permissionDescription) {
        this.permissionDescription = permissionDescription;
    }
}