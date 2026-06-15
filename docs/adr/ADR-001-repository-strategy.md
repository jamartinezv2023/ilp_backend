# ADR-001 - Estrategia de repositorios 2026-2027

## Decision

Mantener el backend como monorepo modular y mantener el frontend como repositorio independiente.

No se separaran los microservicios en repositorios individuales antes del trabajo de campo.

## Razon

El proyecto esta en fase MVP-21A: cierre, estabilizacion, despliegue y preparacion para captura de datos reales.

Separar cada microservicio ahora aumentaria la complejidad de:

- CI/CD.
- Versionado cruzado.
- Integracion.
- Pruebas end-to-end.
- Automatizacion Screenplay BDD Serenity.
- Despliegue completo.
- Trazabilidad academica.

## Estrategia vigente

- Backend: monorepo modular Gradle.
- Frontend: repositorio React independiente.
- E2E futuro: suite Screenplay BDD Serenity como capa transversal.
- Docker/Kubernetes: despliegue orquestado por servicio desde el monorepo.

## Se evaluara separar repositorios solo cuando:

- El dataset institucional este consolidado.
- Exista equipo de desarrollo por servicio.
- Cada microservicio tenga ciclo de vida autonomo.
- El CI/CD actual sea estable.
- La automatizacion E2E este madura.
