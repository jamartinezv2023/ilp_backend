package com.inclusive.authservice.validator;

import com.inclusive.authservice.entity.authorization.Permission;
import com.inclusive.authservice.entity.authorization.Role;
import com.inclusive.authservice.exception.BadRequestException;

import java.util.UUID;

/**
 * Validador central de reglas de autorizaciÃ³n.
 * Mantiene las reglas de negocio desacopladas de los servicios.
 */
public final class AuthorizationValidator {

    private AuthorizationValidator() {
        // Utility class
    }

    /**
     * Valida que el recurso pertenezca al tenant actual.
     */
    public static void validateTenant(UUID currentTenantId, UUID resourceTenantId) {
        if (!currentTenantId.equals(resourceTenantId)) {
            throw new BadRequestException("Resource does not belong to tenant");
        }
    }

    /**
     * Valida que el rol estÃ© activo y no soft-deleted.
     */
    public static void validateActiveRole(Role role) {
        if (!role.isActive() || role.isDeleted()) {
            throw new BadRequestException("Role is inactive or deleted");
        }
    }

    /**
     * Punto de extensiÃ³n futuro (soft-delete / estados).
     */
    public static void validatePermissionActive(Permission permission) {
        // Permission aÃºn no tiene estado ni soft-delete
        // MÃ©todo preparado para evoluciÃ³n futura
    }
}