// âœ… src/main/java/com/inclusive/authservice/config/DataInitializer.java
package com.inclusive.authservice.config;

import com.inclusive.authservice.model.Profile;
import com.inclusive.authservice.repository.ProfileRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer {

    private final ProfileRepository profileRepository;

    public DataInitializer(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @PostConstruct
    public void initProfiles() {
        if (profileRepository.count() == 0) {
            List<Profile> profiles = Arrays.asList(
                    new Profile("Administrador", "Perfil con acceso completo"),
                    new Profile("Profesor", "Perfil para docentes"),
                    new Profile("Estudiante", "Perfil para estudiantes")
            );
            profileRepository.saveAll(profiles);
            System.out.println("âœ… Perfiles inicializados correctamente.");
        }
    }
}










