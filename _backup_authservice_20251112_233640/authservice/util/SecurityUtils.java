package com.inclusive.authservice.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {
    private SecurityUtils() {}
    public static String currentUsername() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        return a != null ? String.valueOf(a.getPrincipal()) : null;
    }
}



