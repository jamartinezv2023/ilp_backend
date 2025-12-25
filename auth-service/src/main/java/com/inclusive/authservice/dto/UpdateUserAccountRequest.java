// Location: auth-service/src/main/java/com/inclusive/authservice/dto/UpdateUserAccountRequest.java
package com.inclusive.authservice.dto;

public class UpdateUserAccountRequest {

    private String email;
    private String password; // opcional

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
}
