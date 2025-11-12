package com.inclusive.authservice.graphql;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.inclusive.authservice.model.User;
import com.inclusive.authservice.model.Profile;
import com.inclusive.authservice.service.UserService;
import com.inclusive.authservice.service.ProfileService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class QueryResolver implements GraphQLQueryResolver {

    private final UserService userService;
    private final ProfileService profileService;

    public QueryResolver(UserService userService, ProfileService profileService) {
        this.userService = userService;
        this.profileService = profileService;
    }

    public List<User> users() {
        return userService.getAllUsers();
    }

    public User userById(Long id) {
        return userService.getUserById(id);
    }

    public List<Profile> profiles() {
        return profileService.getAllProfiles();
    }
}




