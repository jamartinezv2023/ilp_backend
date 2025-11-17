package com.inclusive.authservice.model;

/**
 * Enum para describir metodos de autenticacion soportados por el sistema.
 *
 * Archivo: auth-service/src/main/java/com/inclusive/authservice/model/AuthMethodType.java
 */
public enum AuthMethodType {
    PASSWORD,
    MAGIC_LINK_EMAIL,
    OTP_EMAIL,
    OTP_SMS,
    WEBAUTHN,
    OAUTH_GOOGLE,
    OAUTH_MICROSOFT
}
