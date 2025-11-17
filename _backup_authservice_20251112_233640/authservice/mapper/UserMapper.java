// src/main/java/com/inclusive/authservice/mapper/UserMapper.java
package com.inclusive.authservice.mapper;

import com.inclusive.authservice.model.User;
import com.inclusive.authservice.dto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapper que convierte entre User y UserDTO usando MapStruct.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDTO toDTO(User user);
    User toEntity(UserDTO userDTO);
}






