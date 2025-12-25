// Location: auth-service/src/main/java/com/inclusive/authservice/mapper/UserAccountMapper.java
package com.inclusive.authservice.mapper;

import com.inclusive.authservice.dto.UserAccountDTO;
import com.inclusive.authservice.entity.UserAccount;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserAccountMapper {

    UserAccountDTO toDto(UserAccount entity);

    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "mfaEnabled", ignore = true)
    @Mapping(target = "mfaSecret", ignore = true)
    @Mapping(target = "learningStyleKolb", ignore = true)
    @Mapping(target = "learningStyleFelder", ignore = true)
    @Mapping(target = "kuderPreferences", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    UserAccount toEntity(UserAccountDTO dto);

    @InheritConfiguration(name = "toEntity")
    void updateEntityFromDto(UserAccountDTO dto, @MappingTarget UserAccount entity);
}
