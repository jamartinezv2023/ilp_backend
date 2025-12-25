// Location: auth-service/src/main/java/com/inclusive/authservice/exception/ResourceNotFoundException.java
package com.inclusive.authservice.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
