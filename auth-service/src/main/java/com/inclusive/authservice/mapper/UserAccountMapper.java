// Location: auth-service/src/main/java/com/inclusive/authservice/mapper/UserAccountMapper.java
package com.inclusive.authservice.mapper;

import com.inclusive.authservice.dto.UserAccountDTO;
import com.inclusive.authservice.entity.UserAccount;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserAccountMapper {

    /**
     * ⚠️ Importante:
     * - NO se mapean aquí Felder ni Kuder
     * - Solo el núcleo del agregado UserAccount
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "mfaEnabled", ignore = true)
    @Mapping(target = "mfaSecret", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    UserAccount toEntity(UserAccountDTO dto);
}
