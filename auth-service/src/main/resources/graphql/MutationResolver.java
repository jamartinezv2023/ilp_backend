package com.inclusive.authservice.graphql;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.inclusive.authservice.model.User;
import com.inclusive.authservice.service.UserService;
import com.inclusive.authservice.dto.UserRequestDTO;
import com.inclusive.authservice.mapper.UserMapper;
import org.springframework.stereotype.Component;

@Component
public class MutationResolver implements GraphQLMutationResolver {

    private final UserService userService;
    private final UserMapper userMapper;

    public MutationResolver(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    public User createUser(CreateUserInput input) {
        UserRequestDTO dto = new UserRequestDTO();
        dto.setUsername(input.getUsername());
        dto.setEmail(input.getEmail());
        dto.setPassword(input.getPassword());

        return userService.createUser(dto);
    }

    // Clase interna para mapear input
    public static class CreateUserInput {
        private String username;
        private String email;
        private String password;

        // getters y setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}




