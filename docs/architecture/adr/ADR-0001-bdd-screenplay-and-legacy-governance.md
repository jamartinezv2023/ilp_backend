# ADR-0001: BDD Screenplay and Legacy Governance

## Estado

Aceptada

## Contexto

El backend ILP contiene módulos funcionales, pruebas unitarias, contratos de eventos, CI/CD, Redpanda/Kafka y transactional outbox. La evolución hacia BDD debe realizarse sin romper funcionalidades existentes.

## Decisión

Se adoptará una estrategia incremental:

1. Documentar historias de usuario.
2. Escribir escenarios Gherkin centrados en actores.
3. Crear un módulo BDD independiente.
4. Implementar Serenity BDD con Screenplay.
5. Usar los escenarios como red de seguridad antes de refactorizar legacy.
6. Migrar progresivamente módulos hacia arquitectura hexagonal.

## Consecuencias

- Se conserva el comportamiento funcional existente.
- Se reduce el riesgo de regresiones.
- Se mejora la trazabilidad entre negocio, pruebas y código.
- Se habilita documentación viva mediante reportes Serenity.
