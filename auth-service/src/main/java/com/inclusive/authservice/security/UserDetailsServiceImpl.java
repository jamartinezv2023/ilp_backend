// âœ… src/main/java/com/inclusive/authservice/security/UserDetailsServiceImpl.java
package com.inclusive.authservice.security;

import com.inclusive.authservice.model.User;
import com.inclusive.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return new UserDetailsImpl(user); // asegÃºrate de tener tambiÃ©n esta clase
    }
}




