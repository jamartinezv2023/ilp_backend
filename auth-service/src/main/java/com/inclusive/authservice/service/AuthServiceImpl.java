package com.inclusive.authservice.service;

import com.inclusive.authservice.model.Role;
import com.inclusive.authservice.model.User;
import com.inclusive.authservice.repository.RoleRepository;
import com.inclusive.authservice.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public AuthServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public User registerUser(String email, String password) {
        userRepository.findByEmail(email).ifPresent(u -> {
            throw new IllegalArgumentException("El correo ya est? registrado");
        });

        Role baseRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> {
                    Role r = new Role();
                    r.setName("ROLE_USER");
                    return roleRepository.save(r);
                });

        User user = new User();
        user.setEmail(email);
        // NOTA: en la siguiente fase se encriptar? la contrase?a (PasswordEncoder + JWT)
        user.setPassword(password);
        user.getRoles().add(baseRole);

        return userRepository.save(user);
    }

    @Override
    public List<User> listUsers() {
        return userRepository.findAll();
    }
}
