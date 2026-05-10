# Legacy Governance Strategy

## Propósito

Gestionar código legacy sin romper funcionalidades existentes, conservando lo que funciona y migrando progresivamente hacia una arquitectura modular, verificable y mantenible.

## Principios

1. No eliminar código funcional sin cobertura.
2. No reescribir módulos completos sin pruebas de caracterización.
3. Introducir BDD como contrato funcional antes de refactorizar.
4. Priorizar alta cohesión y bajo acoplamiento.
5. Migrar gradualmente hacia arquitectura hexagonal.
6. Mantener trazabilidad entre historia de usuario, escenario Gherkin, prueba automatizada y evidencia CI/CD.

## Clasificación del código

- Activo: código usado, probado y mantenido.
- Legacy estable: código funcional con bajo riesgo, pero con deuda técnica.
- Legacy crítico: código funcional sin pruebas suficientes.
- Legacy candidato a eliminación: código no usado o duplicado.
- Experimental: código pendiente de validación.

## Estrategia de migración

1. Identificar comportamiento actual.
2. Escribir escenarios Gherkin del comportamiento esperado.
3. Crear pruebas de caracterización si no existen.
4. Refactorizar internamente sin cambiar contrato.
5. Validar con CI.
6. Documentar decisión arquitectónica.

## Regla de seguridad

Todo cambio sobre código legacy debe responder a una historia de usuario o a una deuda técnica registrada.
