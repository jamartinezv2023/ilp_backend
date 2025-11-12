package com.inclusive.authservice.service;

import com.inclusive.authservice.model.Profile;
import com.inclusive.authservice.repository.ProfileRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;

@Service
public class ProfileInitializerService {

    private final ProfileRepository profileRepository;

    public ProfileInitializerService(ProfileRepository profileRepository) {
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
        } else {
            System.out.println("â„¹ï¸ Los perfiles ya existen en la base de datos.");
        }
    }
}











