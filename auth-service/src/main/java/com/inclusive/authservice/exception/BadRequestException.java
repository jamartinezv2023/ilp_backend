// Location: auth-service/src/main/java/com/inclusive/authservice/exception/BadRequestException.java
package com.inclusive.authservice.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
