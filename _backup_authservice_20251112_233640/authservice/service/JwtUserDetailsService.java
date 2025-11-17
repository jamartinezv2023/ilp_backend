package com.inclusive.authservice.service;

import java.util.ArrayList;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Servicio que carga los detalles del usuario para la autenticaciÃ³n JWT.
 * En producciÃ³n, debe integrarse con la base de datos de usuarios.
 */
@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // En un caso real, buscarÃ­a en la base de datos.
        if ("admin".equals(username)) {
            return new User("admin", "$2a$10$4U2p5kVZ2pY1Ih9j9vJ5leMp5Zx8lw6w4NPKchL7lZPS1bVbTHpJq", new ArrayList<>());
        } else {
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        }
    }
}




