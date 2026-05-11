Feature: Autenticación

  Scenario: Inicio de sesión exitoso
    Given el usuario tiene credenciales válidas
    When intenta iniciar sesión
    Then accede correctamente a la plataforma

  Scenario: Credenciales inválidas
    Given el usuario ingresa credenciales no registradas
    When intenta iniciar sesión
    Then visualiza un mensaje indicando que las credenciales no son válidas

  Scenario: Usuario bloqueado
    Given el usuario se encuentra bloqueado
    When intenta iniciar sesión
    Then visualiza un mensaje indicando que el usuario está bloqueado
