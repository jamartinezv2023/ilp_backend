// âœ… src/main/java/com/inclusive/authservice/security/UserDetailsImpl.java
package com.inclusive.authservice.security;

import com.inclusive.authservice.model.User;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * ImplementaciÃ³n personalizada de UserDetails que adapta
 * nuestra entidad User a la estructura que Spring Security entiende.
 */
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private final User user; // Usuario real de nuestra base de datos

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Por ahora devolvemos una lista vacÃ­a (sin roles especÃ­ficos)
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return user.getPassword(); // Retorna la contraseÃ±a encriptada
    }

    @Override
    public String getUsername() {
        return user.getEmail(); // Usamos el email como username
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Cuenta no expira
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Cuenta no bloqueada
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Credenciales no expiran
    }

    @Override
    public boolean isEnabled() {
        return true; // Usuario activo
    }

    /**
     * MÃ©todo adicional para acceder al objeto User completo
     */
    public User getUser() {
        return user;
    }
}







