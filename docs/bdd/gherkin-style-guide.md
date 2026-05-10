# Gherkin Style Guide

## Principio central

Los escenarios deben describir el comportamiento desde la perspectiva de quien interactúa con la plataforma, no desde el sistema.

## Actores válidos

- estudiante
- docente
- orientador
- administrador institucional
- operador académico

## Reglas

1. No usar "el sistema" como protagonista.
2. No depender del diseño visual.
3. No usar adjetivos innecesarios.
4. Cada escenario debe representar un comportamiento verificable.
5. Cada escenario debe ser independiente.
6. Los caminos felices y alternos deben separarse.
7. El lenguaje debe ser comprensible para negocio, QA y desarrollo.

## Plantilla

Feature: Nombre de la capacidad

  Scenario: Resultado esperado
    Given el actor dispone de una condición inicial
    When realiza una acción
    Then observa un resultado verificable

## Ejemplo correcto

Feature: Autenticación

  Scenario: Inicio de sesión exitoso
    Given el usuario tiene credenciales válidas
    When intenta iniciar sesión
    Then accede a su cuenta

  Scenario: Credenciales inválidas
    Given el usuario ingresa credenciales no registradas
    When intenta iniciar sesión
    Then visualiza un mensaje indicando que las credenciales no son válidas
