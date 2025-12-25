package com.inclusive.authservice.service.authorization;

import java.util.UUID;

public interface UserRoleService {

    void assignRoleToUser(UUID userId, UUID roleId);

    void removeRoleFromUser(UUID userId, UUID roleId);
}