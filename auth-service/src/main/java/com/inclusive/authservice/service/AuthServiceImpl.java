package com.inclusive.authservice.service;

import com.inclusive.authservice.dto.AuthResponse;
import com.inclusive.authservice.model.Role;
import com.inclusive.authservice.model.User;
import com.inclusive.authservice.repository.RoleRepository;
import com.inclusive.authservice.repository.UserRepository;
import com.inclusive.authservice.security.JwtTokenService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public AuthServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenService jwtTokenService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public User registerUser(String email, String rawPassword) {
        userRepository.findByEmail(email).ifPresent(u -> {
            throw new IllegalArgumentException("El correo ya está registrado");
        });

        Role baseRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> {
                    Role r = new Role();
                    r.setName("ROLE_USER");
                    return roleRepository.save(r);
                });

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setEnabled(true);
        user.getRoles().add(baseRole);

        return userRepository.save(user);
    }

    @Override
    public AuthResponse login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new BadCredentialsException("Credenciales inválidas");
        }

        List<String> roleNames = user.getRoles()
                .stream()
                .map(Role::getName)
                .toList();

        String accessToken = jwtTokenService.generateAccessToken(user.getEmail(), roleNames);
        String refreshToken = jwtTokenService.generateRefreshToken(user.getEmail(), roleNames);

        AuthResponse response = new AuthResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setExpiresIn(jwtTokenService.getAccessTokenExpirationMs());
        response.setUserId(user.getId());
        response.setEmail(user.getEmail());
        response.setRoles(roleNames);

        return response;
    }

    @Override
    public List<User> listUsers() {
        return userRepository.findAll();
    }
}
