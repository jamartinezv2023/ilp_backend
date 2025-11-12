// src/main/java/com/inclusive/authservice/mapper/ProfileDTOMapper.java
package com.inclusive.authservice.mapper;

import com.inclusive.authservice.dto.ProfileDTO;
import com.inclusive.authservice.model.Profile;
import org.springframework.stereotype.Component;

@Component
public class ProfileDTOMapper {

    /**
     * Convierte una entidad Profile a un DTO.
     */
    public ProfileDTO toDTO(Profile profile) {
        if (profile == null) {
            return null;
        }
        return new ProfileDTO(
                profile.getId(),
                profile.getNombre(),        // ðŸ‘ˆ cambia aquÃ­ si tu campo se llama distinto
                profile.getDescripcion()  // ðŸ‘ˆ cambia aquÃ­ si tu campo se llama distinto
        );
    }

    /**
     * Convierte un DTO a una entidad Profile.
     */
    public Profile toEntity(ProfileDTO dto) {
        if (dto == null) {
            return null;
        }
        return new Profile(
                dto.getNombre(),
                dto.getDescripcion()
        );
    }
}





