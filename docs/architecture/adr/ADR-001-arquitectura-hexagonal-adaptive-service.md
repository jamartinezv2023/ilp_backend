# ADR-001: Adopción incremental de arquitectura hexagonal

## Estado
Aceptado.

## Contexto
El módulo adaptive-education-service concentra lógica de recomendación, inferencia, eventos y estrategias pedagógicas. Para evitar alto acoplamiento y crecimiento desordenado, se adopta arquitectura hexagonal de forma incremental.

## Decisión
Separar el módulo en:
- domain: reglas de negocio puras.
- application: casos de uso y puertos.
- infrastructure: adaptadores externos.
- api: entrada HTTP.

## Consecuencias
- Mayor testabilidad.
- Bajo acoplamiento.
- Alta cohesión.
- Mejor alineación con calidad, SonarCloud y pruebas automatizadas.
