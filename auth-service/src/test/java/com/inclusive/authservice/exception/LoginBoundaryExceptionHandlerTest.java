package com.inclusive.authservice.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MissingRequestHeaderException;

class LoginBoundaryExceptionHandlerTest {
    @Test
    void invalidCredentialsAreUnauthorizedWithoutRevealingAccountDetails() {
        var response = new LoginBoundaryExceptionHandler().invalidCredentials(
                new BadCredentialsException("Sensitive account detail"));
        assertEquals(401, response.getStatusCode().value());
        assertEquals("Invalid credentials", response.getBody().get("message"));
        assertFalse(response.getBody().toString().contains("Sensitive"));
    }

    @Test
    void missingRequiredHeaderIsBadRequest() throws NoSuchMethodException {
        var parameter = new MethodParameter(getClass().getDeclaredMethod("header", String.class), 0);
        var response = new LoginBoundaryExceptionHandler().missingHeader(
                new MissingRequestHeaderException("X-Tenant-Id", parameter));
        assertEquals(400, response.getStatusCode().value());
        assertEquals("BAD_REQUEST", response.getBody().get("error"));
    }

    void header(String value) { }
}
